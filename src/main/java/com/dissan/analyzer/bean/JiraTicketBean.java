package com.dissan.analyzer.bean;

import com.dissan.analyzer.model.JiraTicket;
import com.dissan.analyzer.model.Release;

import java.util.ArrayList;
import java.util.List;

public class JiraTicketBean implements TicketBeanApi {
    private List <JiraTicket> bugInfoList = new ArrayList<>();
    private final TicketJiraBean releasesBean;
    private final String projectName;
    public JiraTicketBean(String projectName, TicketJiraBean releases) {
        this.projectName = projectName;
        this.releasesBean = releases;
    }

    public JiraTicketBean(String projectName, List<JiraTicket> bugBeans, TicketJiraBean releases) {
        this.projectName = projectName;
        this.bugInfoList = bugBeans;
        this.releasesBean = releases;
    }

    public  void add(JiraTicket jiraTicket) {
        this.bugInfoList.add(jiraTicket);
    }

    @Override
    public String getBugVersionInfo() {
        StringBuilder builder = new StringBuilder("bug info for: ");
        builder.append('[').append(this.projectName).append(']').append('\n');
        for (JiraTicket b:
             this.bugInfoList) {
            builder.append(b.getKey()).append('{').append('\n').append('\t').append('[');
            if (b.getInjectedVersion() != null){
                builder.append("\"iv\": ").append(b.getInjectedVersion().getVersionId()).append(", ");
            }
            builder.append("\"ov\": ").append(b.getOpeningVersion().getVersionId()).append(", \"fv\": ").append(b.getFixedVersion().getVersionId()).append(']').append('\n').append('}').append('\n');
        }
        return builder.toString();
    }

    public List<JiraTicket> getBugWithIv(){
        List<JiraTicket> jiraTicketList = new ArrayList<>();

        for (JiraTicket jb:
             this.bugInfoList) {
            if (jb.getInjectedVersion() != null){
                jiraTicketList.add(jb);
            }
        }
        return jiraTicketList;
    }

    @Override
    public int getBugSize() {
        return this.bugInfoList.size();
    }

    public String getProjectName() {
        return this.projectName;
    }

    public String getReleases() {
        return this.releasesBean.getBugReleases();
    }

    public List<Release> getReleasesList() {
        return this.releasesBean.getReleases();
    }

    public List<JiraTicket> getBugs() {
        return this.bugInfoList;
    }
}
