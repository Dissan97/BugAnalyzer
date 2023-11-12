package com.dissan.analyzer.system;

import org.eclipse.jgit.revwalk.RevCommit;

import java.util.List;
import java.util.Map;

public class ReleaseCommits {

    private Release release;
    private List<RevCommit> revCommits;
    private RevCommit lastCommit;
    Map<String, String> javaClasses;

    public ReleaseCommits(Release release, List<RevCommit> revCommits, RevCommit lastCommit) {
        this.release = release;
        this.revCommits = revCommits;
        this.lastCommit = lastCommit;
        this.javaClasses = null;
    }

    public Release getRelease() {
        return release;
    }

    public void setRelease(Release release) {
        this.release = release;
    }

    public List<RevCommit> getRevCommits() {
        return revCommits;
    }

    public void setRevCommits(List<RevCommit> revCommits) {
        this.revCommits = revCommits;
    }

    public RevCommit getLastCommit() {
        return lastCommit;
    }

    public void setLastCommit(RevCommit lastCommit) {
        this.lastCommit = lastCommit;
    }

    public Map<String, String> getJavaClasses() {
        return javaClasses;
    }

    public void setJavaClasses(Map<String, String> javaClasses) {
        this.javaClasses = javaClasses;
    }
}
