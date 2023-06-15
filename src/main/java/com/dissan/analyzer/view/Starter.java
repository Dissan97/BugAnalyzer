package com.dissan.analyzer.view;

import com.dissan.analyzer.bean.JiraTicketBean;
import com.dissan.analyzer.controller.BugRetriever;
import com.dissan.analyzer.controller.BugRetrieverAPI;
import com.dissan.analyzer.controller.ProportionCalculator;
import com.dissan.analyzer.model.JiraTicket;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Starter {

    private static final String PROJECT = "BOOKKEEPER";
    private static final String PROJECT_TWO = "OPENJPA";
    private static final String COLD_START_CONF = "coldStartQueries.json";

    //todo Adjust this with some Map need to iterate to gets the proportion by using the cold start...
    private static final Map<String,List<JiraTicket>> COLD_START_TICKETS = new HashMap<>();
    private static void init() throws IOException {
        BugRetriever retriever = new BugRetriever();
        retriever.start(COLD_START_CONF);
        for (String k:
             retriever.getBugBeanMap().keySet()) {
            Starter.COLD_START_TICKETS.put(k, retriever.getBugBean(k).getBugWithIv());
        }
    }
    private static @NotNull BugRetriever collectData() throws IOException {
        BugRetriever bugRetriever = new BugRetriever();
        bugRetriever.start();
        return bugRetriever;
    }

    private static void execute(@NotNull JiraTicketBean ticketBean) throws IOException {
        double p;
        List<JiraTicket> tickets = ticketBean.getBugWithIv();
        if (tickets.size() >= 5){
            p = ProportionCalculator.calculateProportion(tickets);
        }else {
            p = Starter.getColdStartProportion();
        }


    }

    private static double getColdStartProportion() {
        double p = 0;
        double counter = 0;
        List<Double> median = new ArrayList<>();
        for (String k:
             Starter.COLD_START_TICKETS.keySet()) {
            for (int i = 0; i < COLD_START_TICKETS.get(k).size(); i++) {
                int size = Starter.COLD_START_TICKETS.get(k).size();
                int index = (i + 5)  < size ? (i + 5) : (size - 1) ;
                median.add(ProportionCalculator.calculateProportion(Starter.COLD_START_TICKETS.get(k).subList(0, index)));
            }

            int size = median.size();
            p += size % 2 == 1 ? median.get(size/2) : ((median.get(size / 2) + median.get((size / 2) + 1)) / 2);
            counter++;
        }
        return p / counter;
    }

    /**
     * What the view should do
     * @param args args
     * @throws IOException exception
     */
    public static void main(String[] args) throws IOException {
        Starter.init();
        BugRetrieverAPI bugRetriever = Starter.collectData();
        Starter.execute(bugRetriever.getBugBean(Starter.PROJECT));
        Starter.execute(bugRetriever.getBugBean(Starter.PROJECT_TWO));
    }


}
