package com.dissan.analyzer.utils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import java.io.IOException;

public class JiraApiRequest {

    private final String projectName;

    private boolean normalSearch = false;
    private String fields = "";
    private int start = 0;
    private int total;

    public JiraApiRequest(String projectName, String field, int start) {
        this.projectName = projectName;
        this.setFields(field);
        this.setStart(start);
        this.setTotal();
    }

    public void isBug(){
        this.normalSearch = false;
    }

    public void setFields(@NotNull String fields) {
        if (!fields.isEmpty()) {
            this.fields = fields;
        }
    }

    public void setStart(int start) {
        this.start = start;
    }


    private void setTotal() {
        try {
            JSONObject jsonObject = JSONet.readJsonFromUrl(this.toString());
            this.total = jsonObject.getInt("total");
        } catch (IOException e) {
            this.total = 0;
        }
    }

    public int getStart() {
        return this.start;
    }
    public int getTotal() {
        return total;
    }

    public String getProjectName() {
        return projectName;
    }

    @Override
    public String toString(){
        String request;
        int maxRes = 50;
        if (normalSearch) {
            request = "https://issues.apache.org/jira/rest/api/2/project/";
            return request + this.projectName;
        }
        request = "https://issues.apache.org/jira/rest/api/2/search?jql=project%20%3D%20";
        String bug = "%20AND%20issuetype%20%3D%20Bug%20AND%20(%22status%22%20%3D%22resolved%22%20OR%20%22status" +
                "%22%20%3D%20%22closed%22)%20AND%20%20%22resolution%22%20%3D%20%22fixed%22%20&";

        if ((this.start + maxRes) > this.total && total != 0){
            maxRes = this.total - this.start;
        }
        return request + this.projectName + bug + this.fields + "&startAt=" + this.start + "&maxResults=" + maxRes;

    }


}
