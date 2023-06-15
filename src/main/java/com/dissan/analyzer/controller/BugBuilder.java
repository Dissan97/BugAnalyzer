package com.dissan.analyzer.controller;

import com.dissan.analyzer.bean.TicketJiraBean;
import com.dissan.analyzer.model.JiraTicket;
import com.dissan.analyzer.model.Release;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * Static class that has no status it used to build model.JiraTicket
 */
public class BugBuilder {

    /**
     * It cannot be instantiated
     */
    private BugBuilder(){}

    /**
     * Method used to build jiraTicket list
     */
    public static JiraTicket getBugInstance(@NotNull TicketJiraBean releases, @NotNull JSONObject fields, String key) {
        String releaseDate = fields.get("resolutiondate").toString();
        String creationDate = fields.get("created").toString();
        List<Release> ticketsJira = releases.getReleases();

        Release openingVersion = getVersionInfo(ticketsJira, creationDate);
        Release fixedVersion = getVersionInfo(ticketsJira, releaseDate);
        Release injectedVersion = null;
        List<Release> affectedVersions;
        JiraTicket jiraTicket = null;

        if (openingVersion != null && fixedVersion != null ) {
            if (openingVersion.getdDate().before(fixedVersion.getdDate()) || openingVersion.getdDate().equals(fixedVersion.getdDate())) {
                affectedVersions = getAffectedVersion(fields.getJSONArray("versions"), ticketsJira);
                if (!affectedVersions.isEmpty()) {
                    Comparator<Release> ordering = Comparator.comparing(Release::getdDate);
                    affectedVersions.sort(ordering);
                    injectedVersion = affectedVersions.get(0);
                }
                jiraTicket = new JiraTicket(key, openingVersion, fixedVersion, affectedVersions);
                if (injectedVersion != null) {
                    jiraTicket.setInjectedVersion(injectedVersion);
                }
            }
        }
        return jiraTicket;
    }

    private static @NotNull List<Release> getAffectedVersion(@NotNull JSONArray fields, List<Release> ticketsJira) {
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

    private static @Nullable Release getVersionInfo(@NotNull List<Release> releaseList, String date) {

        Logger logger = Logger.getLogger(BugBuilder.class.getSimpleName()+".getVersionInfo");
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
            logger.warning(e.getMessage());
        }



        return null;

    }
}
