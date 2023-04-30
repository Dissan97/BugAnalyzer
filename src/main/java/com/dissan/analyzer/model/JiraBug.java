package com.dissan.analyzer.model;


import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;

public class JiraBug {

    private String key;
    private Release fixedVersion;
    private Release openingVersion;
    private Release injectedVersion = null;
    private List<Release> affectedVersion;

    public JiraBug(String key, Release ov, Release fv, List<Release> av) {
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

    public List<Release> getAffectedVersion() {
        return affectedVersion;
    }

    public Date getFixedDate(){
        return this.fixedVersion.getdDate();
    }

    public int getAffectedSize(){
        return this.affectedVersion.size();
    }

    public String getKey() {
        return this.key;
    }

    public int getIv() {
        int index = -1;
        if(this.injectedVersion != null) {
            index = this.injectedVersion.getIndex();
        }
        return index;

    }

    public int getFv(){
        int index = -1;
        if(this.fixedVersion != null) {
            index = this.fixedVersion.getIndex();
        }
        return index;

    }

    public int getOv(){
        int index = -1;
        if(this.openingVersion != null) {
            index = this.openingVersion.getIndex();
        }
        return index;

    }

}
