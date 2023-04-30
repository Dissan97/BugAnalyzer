package com.dissan.analyzer.bean;

import com.dissan.analyzer.model.JiraBug;
import com.dissan.analyzer.model.Release;

import java.util.*;

public class JiraBugBean implements BugBeanAPI{
    private List <JiraBug> bugInfoList = new ArrayList<>();
    private final TicketJiraBean releasesBean;
    private final String projectName;

    public JiraBugBean(String projectName,TicketJiraBean releases) {
        this.projectName = projectName;
        this.releasesBean = releases;
    }

    public JiraBugBean(String projectName, List<JiraBug> bugBeans, TicketJiraBean releases) {
        this.projectName = projectName;
        this.bugInfoList = bugBeans;
        this.releasesBean = releases;
    }

    public  void add(JiraBug bugInfo) {
        this.bugInfoList.add(bugInfo);
    }

    @Override
    public String getBugVersionInfo() {
        StringBuilder builder = new StringBuilder("bug info for: ");
        builder.append('[').append(this.projectName).append(']').append('\n');
        for (JiraBug b:
             this.bugInfoList) {
            builder.append(b.getKey()).append('{').append('\n').append('\t').append('[');
            if (b.getInjectedVersion() != null){
                builder.append("\"iv\": ").append(b.getInjectedVersion().getVersionId()).append(", ");
            }
            builder.append("\"ov\": ").append(b.getOpeningVersion().getVersionId()).append(", \"fv\": ").append(b.getFixedVersion().getVersionId()).append(']').append('\n').append('}').append('\n');
        }
        return builder.toString();
    }

    public List<JiraBug> getBugInfoList(){
        return this.bugInfoList;
    }

    public List<JiraBug> getBugWithIv(){
        List<JiraBug> jiraBugList = new ArrayList<>();

        for (JiraBug jb:
             this.bugInfoList) {
            if (jb.getInjectedVersion() != null && jb.getFixedVersion().getIndex() != jb.getOpeningVersion().getIndex()){
                jiraBugList.add(jb);
            }
        }

        jiraBugList.sort(Comparator.comparingInt(o -> {
            String key = o.getKey();
            return Integer.parseInt(key.split("-")[1]);
        }));

        return jiraBugList;
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
}
