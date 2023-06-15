package com.dissan.analyzer.model;


import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;

public class JiraTicket {

    private String key;
    private final Release fixedVersion;
    private final Release openingVersion;
    private Release injectedVersion = null;
    private List<Release> affectedVersion;

    public JiraTicket(String key, Release ov, Release fv, List<Release> av) {
        this.key = key;
        this.openingVersion = ov;
        this.fixedVersion = fv;
        this.affectedVersion = av;
    }

    public void setInjectedVersion(@NotNull Release iv) {
        this.injectedVersion = iv;
    }

    public Release getInjectedVersion() {
        return this.injectedVersion;
    }

    public Release getOpeningVersion() {
        return openingVersion;
    }

    public Release getFixedVersion() {
        return this.fixedVersion;
    }



    public String getKey() {
        return this.key;
    }

    public List<Release> getAffectedVersion() {
        return affectedVersion;
    }

    public Date getFixedDate(){
        return this.fixedVersion.getdDate();
    }

    public void setAffectedVersion(@NotNull List<Release> affectedVersion) {
        this.affectedVersion = affectedVersion;
    }
}
