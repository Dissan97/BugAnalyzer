package com.dissan.analyzer.bean;

import com.dissan.analyzer.model.Release;

import java.util.ArrayList;
import java.util.List;

public class TicketJiraBean {
    private final String projectName;
    private final List<Release> releaseList = new ArrayList<>();

    public TicketJiraBean(String projectName) {
        this.projectName = projectName;
    }

    public void addRelease(Release release){
        this.releaseList.add(release);
    }

    public List<Release> getReleases() {
        return releaseList;
    }

    public String getBugReleases(){
        StringBuilder builder = new StringBuilder();
        builder.append('"').append(this.projectName).append('"').append(':').append('[').append('\n');
        int index = 1;
        for (Release b: this.getReleases()){
            builder.append('\t');
            String end = "]\n";
            if (index < this.getReleases().size()) {
                end = ",\n";
            }
            builder.append(b).append(end);
            index ++;
        }
        return builder.toString();
    }
}
