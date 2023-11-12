package com.dissan.analyzer.system;

import com.dissan.analyzer.api.BugRetrieverAPI;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Class used to retrieve jira ticket information
 */

public class BugRetriever implements BugRetrieverAPI {
    private final Map<LocalDateTime, String> releaseNames = new HashMap<>();
    private final HashMap<LocalDateTime, String> releaseID = new HashMap<>();
    private final ArrayList<LocalDateTime> releases = new ArrayList<>();
    private final Map<String, TicketJiraBean> ticketJiraBeanMap = new HashMap<>();
    private final Map <String, JiraTicketBean> bugBeanMap = new HashMap<>();

    public void start() throws IOException {
        this.start("conf.json");
    }

    public void start(String conf) throws IOException {
        ConfigProjectsJson configProjectsJson = new  ConfigProjectsJson(conf);
        List<JiraApiRequest> apiRequest = configProjectsJson.getApiRequests();
        for (JiraApiRequest a: apiRequest){
            this.doQuery(a);
        }
    }

    /**
     * This method does query to retrieve information about issues
     * @param apiRequest Json file request are parsed and executed
     */
    private void doQuery(@NotNull JiraApiRequest apiRequest) throws IOException {
        int startIndex = apiRequest.getStart();
        int total = apiRequest.getTotal();
        int increment = 50;
        Map <String, JSONObject> fieldMap = new HashMap<>();
        for (; startIndex < total + increment; startIndex += increment){
            JSONObject jObject = JSONet.readJsonFromUrl(apiRequest.toString());
            setupReleases(jObject.getJSONArray("issues"), fieldMap);
            apiRequest.setStart(startIndex);
        }
        String projectName = apiRequest.getProjectName();
        this.setupBeans(projectName);
        //SETUP BUGS
        this.setupBugs(fieldMap, projectName);

        fieldMap.clear();
    }

    /**
     * This method is needed to set up releases and their relative bug
     * @param issueArray issue array got from method query
     */
    private void setupReleases(@NotNull JSONArray issueArray, Map <String, JSONObject> fieldMap) {
        String kResolutionDate = "releaseDate";

        for(int index = 0; index < issueArray.length(); index++){

            JSONArray jaVersions = issueArray.getJSONObject(index).getJSONObject("fields").getJSONArray("versions");
            for (int i = 0; i < jaVersions.length(); i++ ) {
                String name = "";
                String id = "";
                if (jaVersions.getJSONObject(i).has(kResolutionDate)) {
                    if (jaVersions.getJSONObject(i).has("name"))
                        name = jaVersions.getJSONObject(i).getString("name");
                    if (jaVersions.getJSONObject(i).has("id"))
                        id = jaVersions.getJSONObject(i).get("id").toString();
                    addRelease(jaVersions.getJSONObject(i).get(kResolutionDate).toString(), name, id);
                }
            }
            releases.sort(LocalDateTime::compareTo);
            JSONObject fields = issueArray.getJSONObject(index).getJSONObject("fields");
            fieldMap.put(issueArray.getJSONObject(index).getString("key"), fields);
        }

    }

    private void setupBugs(@NotNull Map <String, JSONObject> fieldMap, String projectName){

        Set<String> keys = fieldMap.keySet();
        System.out.println(keys.size());
        JiraTicketBean bugBean = new JiraTicketBean(projectName, this.ticketJiraBeanMap.get(projectName));
        this.bugBeanMap.put(projectName, bugBean);

        for (String key: keys){
            JSONObject field = fieldMap.get(key);
            JiraTicket jiraTicket = BugBuilder.getBugInstance(this.ticketJiraBeanMap.get(projectName), field, key);
            addBugToMap(projectName, jiraTicket);
        }

        //Need to order the list of map -> by fixed date...

        this.bugBeanMap.get(projectName).getBugs().sort(Comparator.comparing(JiraTicket::getFixedDate));

    }

    private void addBugToMap(String projectName, JiraTicket jiraTicket){
        //todo add also bug that does not contain injected version
        if (jiraTicket != null) {
            if (jiraTicket.getInjectedVersion() != null) {
                if (!jiraTicket.getFixedVersion().equals(jiraTicket.getInjectedVersion()) || !jiraTicket.getFixedVersion().equals(jiraTicket.getOpeningVersion())) {
                    this.bugBeanMap.get(projectName).add(jiraTicket);
                }
            }else {
                this.bugBeanMap.get(projectName).add(jiraTicket);
            }
        }
    }

    private void setupBeans(String projectName){
        TicketJiraBean ticketJiraBean = new TicketJiraBean(projectName);
        int index = 1;
        for (LocalDateTime release : this.releases) {
            String rID = this.releaseID.get(release);
            String rName = this.releaseNames.get(release);
            String rDate = release.toString();
            Release ticketJira = new Release(rID, rName, rDate, index++);
            ticketJiraBean.addRelease(ticketJira);
        }
        this.ticketJiraBeanMap.put(projectName, ticketJiraBean);
    }

    private void addRelease(String strDate, String name, String id) {
        LocalDate date = LocalDate.parse(strDate);
        LocalDateTime dateTime = date.atStartOfDay();
        if (!releases.contains(dateTime))
            releases.add(dateTime);
        releaseNames.put(dateTime, name);
        releaseID.put(dateTime, id);
    }

    //todo adjust this bean method
    public TicketJiraBean getJiraTicketBean(String projectName){
        if (this.ticketJiraBeanMap.containsKey(projectName)){
            return this.ticketJiraBeanMap.get(projectName);
        }
        return null;
    }

    @Override
    public JiraTicketBean getBugBean(String projectName) {
        if (this.bugBeanMap.containsKey(projectName)){
            return this.bugBeanMap.get(projectName);
        }
        return null;
    }

    /**
     * Method used to refresh all the data
     */

    public void close(){
        this.ticketJiraBeanMap.clear();
        this.releaseID.clear();
        this.releaseNames.clear();
        this.releases.clear();
        this.bugBeanMap.clear();
    }

    public Map<String, JiraTicketBean> getBugBeanMap() {
        return this.bugBeanMap;
    }
}
