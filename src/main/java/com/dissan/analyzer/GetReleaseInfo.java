package com.dissan.analyzer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;


public class GetReleaseInfo {

    private static Map<LocalDateTime, String> releaseNames;
    private static HashMap<LocalDateTime, String> releaseID;
    private static ArrayList<LocalDateTime> releases;

    private static final String RELEASE_DATE = "releaseDate";

    private static final Logger LOGGER = Logger.getLogger(GetReleaseInfo.class.getSimpleName());
    private static final String VERSION_FILE = "VersionInfo.csv";
    private static final String BUGGY_FILE = "BuggyAnalyzer.csv";

    public static void main(String[] args) {

        for (String project : ConfigProjectInitialization.getProjectsToAnalyze()){
            getProjectJsonFromUrl(project);
            getProjectBugsFromUrl(project);
        }
    }

    public static void getProjectBugsFromUrl(String projectName){
        JSONObject object;
        String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"+projectName+"%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&fields=key,resolutiondate,affectedVersion,versions,created";
        releases = new ArrayList<>();
        releaseNames = new HashMap<>();
        releaseID = new HashMap<> ();

        try {
            object = readJsonFromUrl(url);
            JSONArray array = object.getJSONArray("issues");
            JSONObject json = null;
            for (int index = 0; index < array.length(); index++) {
                json = array.getJSONObject(index).getJSONObject("fields");
                JSONArray versions = json.getJSONArray("versions");
                for (int i = 0; i < versions.length(); i++ ) {
                    String name = "";
                    String id = "";
                    if(versions.getJSONObject(i).has(RELEASE_DATE)) {
                        if (versions.getJSONObject(i).has("name"))
                            name = versions.getJSONObject(i).get("name").toString();
                        if (versions.getJSONObject(i).has("id"))
                            id = versions.getJSONObject(i).get("id").toString();
                        addRelease(versions.getJSONObject(i).get(RELEASE_DATE).toString(),
                                name,id);
                    }
                }

            }

            assert json != null;
            createFileCSV(projectName, BUGGY_FILE);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static  void getProjectJsonFromUrl(String projectName) {
        String url = "https://issues.apache.org/jira/rest/api/2/project/" + projectName;
        JSONObject json = null;
        try {
            json = readJsonFromUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert json != null;

        releases = new ArrayList<>();
        int i;
        JSONArray versions = json.getJSONArray("versions");

        releaseNames = new HashMap<>();
        releaseID = new HashMap<> ();
        for (i = 0; i < versions.length(); i++ ) {
            String name = "";
            String id = "";
            if(versions.getJSONObject(i).has(RELEASE_DATE)) {
                if (versions.getJSONObject(i).has("name"))
                    name = versions.getJSONObject(i).get("name").toString();
                if (versions.getJSONObject(i).has("id"))
                    id = versions.getJSONObject(i).get("id").toString();
                addRelease(versions.getJSONObject(i).get(RELEASE_DATE).toString(),
                        name,id);
            }
        }
        // order releases by date

        createFileCSV(projectName,VERSION_FILE);
    }

    public static void createFileCSV(String projectName, String filename){

        //@Override
        releases.sort(LocalDateTime::compareTo);
        if (releases.size() < 6)
            return;
        //Name of CSV for output
        String path = Objects.requireNonNull(GetReleaseInfo.class.getResource("")).getPath();
        String outName = path + projectName + filename;
        //TRY WITH RESOURCES JAVA
        try (FileWriter fileWriter = new FileWriter(outName)){
            fileWriter.append("Index,Version ID,Version Name,Date");
            fileWriter.append("\n");
            for (int i = 0; i < releases.size(); i++) {
                int index = i + 1;
                fileWriter.append(Integer.toString(index));
                fileWriter.append(",");
                fileWriter.append(releaseID.get(releases.get(i)));
                fileWriter.append(",");
                fileWriter.append(releaseNames.get(releases.get(i)));
                fileWriter.append(",");
                fileWriter.append(releases.get(i).toString());
                fileWriter.append("\n");
            }
            fileWriter.flush();
        } catch (IOException e) {
            LOGGER.info("Error in csv writer");
            e.printStackTrace();
        }
    }

    public static void addRelease(String strDate, String name, String id) {
        LocalDate date = LocalDate.parse(strDate);
        LocalDateTime dateTime = date.atStartOfDay();
        if (!releases.contains(dateTime))
            releases.add(dateTime);
        releaseNames.put(dateTime, name);
        releaseID.put(dateTime, id);
    }


    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            return new JSONObject(jsonText);
        }
    }

    @org.jetbrains.annotations.NotNull
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }


}