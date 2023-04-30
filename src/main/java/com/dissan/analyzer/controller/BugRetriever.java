package com.dissan.analyzer.controller;

import com.dissan.analyzer.bean.JiraBugBean;
import com.dissan.analyzer.bean.TicketJiraBean;
import com.dissan.analyzer.model.JiraBug;
import com.dissan.analyzer.utils.JiraApiRequest;
import com.dissan.analyzer.model.Release;
import com.dissan.analyzer.utils.ConfigProjectsJson;
import com.dissan.analyzer.utils.JSONet;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class BugRetriever implements BugRetrieverAPI{
    private final Map<LocalDateTime, String> releaseNames = new HashMap<>();
    private final HashMap<LocalDateTime, String> releaseID = new HashMap<>();
    private final ArrayList<LocalDateTime> releases = new ArrayList<>();
    private final Map<String, TicketJiraBean> ticketJiraBeanMap = new HashMap<>();
    private final Map <String, JiraBugBean> bugBeanMap = new HashMap<>();

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
        JiraBugBean bugBean = new JiraBugBean(projectName, this.ticketJiraBeanMap.get(projectName));
        this.bugBeanMap.put(projectName, bugBean);

        for (String key: keys){
            JSONObject field = fieldMap.get(key);
            JiraBug jiraBug = BugBuilder.getBugInstance(this.ticketJiraBeanMap.get(projectName), field, key);
            addBugToMap(projectName, jiraBug);
        }

    }

    private void addBugToMap(String projectName, JiraBug jiraBug){
        //todo add also bug that does not contain injected version
        if (jiraBug != null) {
            if (jiraBug.getInjectedVersion() != null) {
                if (!jiraBug.getFixedVersion().equals(jiraBug.getInjectedVersion()) || !jiraBug.getFixedVersion().equals(jiraBug.getOpeningVersion())) {
                    this.bugBeanMap.get(projectName).add(jiraBug);
                }
            }else {
                this.bugBeanMap.get(projectName).add(jiraBug);
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

    @Override
    public TicketJiraBean getJiraTicketBean(String projectName){
        if (this.ticketJiraBeanMap.containsKey(projectName)){
            return this.ticketJiraBeanMap.get(projectName);
        }
        return null;
    }

    @Override
    public JiraBugBean getBugBean(String projectName) {
        if (this.bugBeanMap.containsKey(projectName)){
            return this.bugBeanMap.get(projectName);
        }
        return null;
    }

    public void close(){
        this.ticketJiraBeanMap.clear();
        this.releaseID.clear();
        this.releaseNames.clear();
        this.releases.clear();
        this.bugBeanMap.clear();
    }


    public Map<String, JiraBugBean> getBugMap() {
        return this.bugBeanMap;
    }
}
