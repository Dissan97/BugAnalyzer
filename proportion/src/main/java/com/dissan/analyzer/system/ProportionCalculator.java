package com.dissan.analyzer.system;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class used to do proportion in Jira ticket -> P = (FV - IV) / (FV - OV) where FV != OV.
 * Index of releases must be used to retrieve correct FV.
 * Where FV must be sorted by their release date
 */

public class ProportionCalculator {

    /**
     * Private constructor this class expose just static methods callable from other instances
     */
    private ProportionCalculator(){}

    private static final String COLD_START_QUERIES = "coldStartQueries.json";

    /**
     * Method used to retrieve projects to do cold start proportion method
     * @return Returning coldStart projects retrieving projects
     */
    public static @NotNull List<JiraTicket> coldStartProportion() throws JSONException, IOException, ParseException {

        BugRetriever retriever = new BugRetriever();
        retriever.start(COLD_START_QUERIES);

        /*
        List<JiraTicket> allConsistentTickets = new ArrayList<>();
        RetrieveJiraInfo retJiraInfo = new RetrieveJiraInfo(proj.toString());
        List<Release> coldStartReleases = retJiraInfo.retrieveReleases();
        List<JiraTicket> coldStartTickets = retJiraInfo.retrieveIssues(coldStartReleases);
        List<JiraTicket> coldStartConsistentTickets = retJiraInfo.retrieveConsistentIssues(coldStartTickets, coldStartReleases);

        allConsistentTickets.addAll(coldStartConsistentTickets);
        return allConsistentTickets;
        */

        return new ArrayList<>();
    }

    /**
     *
     * @param issues ticket to calculate the proportion
     * @return proportion value
     */

    public static double calculateProportion(@NotNull List<JiraTicket> issues) {

        Map<Integer, List<Double>> proportionMap = new HashMap<>();

        //CALCULATE THE PROPORTION AND ADJUST FOR EACH RELEASE
        //We are calculating the proportion value P for each ticket in the list
        List<Double> doubles = new ArrayList<>();
        //Needed to calculate the proportion mean
        int actualIndex = 0;
        for(JiraTicket issue : issues) {

            //P = (FV-IV)/(FV-OV)
            double prop = (1.0)*(
                    issue.getFixedVersion().getIndex()-issue.getInjectedVersion().getIndex())/
                    (issue.getFixedVersion().getIndex()-issue.getOpeningVersion().getIndex()
            );

            if (actualIndex != issue.getFixedVersion().getIndex()){
                actualIndex = issue.getFixedVersion().getIndex();
                doubles = new ArrayList<>();
                proportionMap.put(actualIndex, doubles);
            }

            doubles.add(prop);
        }

        return 0.0;

    }




}
