package com.dissan.analyzer.controller;

import com.dissan.analyzer.bean.TicketJiraBean;
import com.dissan.analyzer.model.JiraBug;
import com.dissan.analyzer.model.Release;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
public class BugBuilder {

    private BugBuilder(){}


    public static JiraBug getBugInstance(@NotNull TicketJiraBean releases, @NotNull JSONObject fields, String key) {
        String releaseDate = fields.get("resolutiondate").toString();
        String creationDate = fields.get("created").toString();
        List<Release> ticketsJira = releases.getReleases();

        Release openingVersion = getVersionInfo(ticketsJira, creationDate);
        Release fixedVersion = getVersionInfo(ticketsJira, releaseDate);
        Release injectedVersion = null;
        List<Release> affectedVersions = null;
        JiraBug bug = null;

        if (openingVersion != null && fixedVersion != null && (openingVersion.getdDate().before(fixedVersion.getdDate()) || openingVersion.getdDate().equals(fixedVersion.getdDate()))) {
                affectedVersions = getAffectedVersion(fields.getJSONArray("versions"), ticketsJira);
                if (!affectedVersions.isEmpty()) {
                    Comparator<Release> ordering = Comparator.comparing(Release::getdDate);
                    affectedVersions.sort(ordering);
                    injectedVersion = affectedVersions.get(0);
                }
                bug = new JiraBug(key, openingVersion, fixedVersion, affectedVersions);
                if (injectedVersion != null) {
                    bug.setInjectedVersion(injectedVersion);
                }

        }
        return bug;
    }

    private static List<Release> getAffectedVersion(JSONArray fields, List<Release> ticketsJira) {
        List<Release> retListRelease = new ArrayList<>();
        for (int i = 0; i < fields.length(); i++) {
            for (Release r: ticketsJira
                 ) {
                if (fields.getJSONObject(i).getString("name").equals(r.getVersionId())){
                    retListRelease.add(r);
                }
            }

        }

        return retListRelease;
    }

    private static Release getVersionInfo(List<Release> releaseList, String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Date dDate;

        try {
            dDate = sdf.parse(date);
            for (Release release: releaseList){
                if (dDate.before(release.getdDate())){
                    return release;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }



        return null;

    }
}
