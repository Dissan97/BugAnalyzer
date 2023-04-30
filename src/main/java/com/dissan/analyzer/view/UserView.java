package com.dissan.analyzer.view;

import com.dissan.analyzer.bean.JiraBugBean;
import com.dissan.analyzer.bean.TicketJiraBean;
import com.dissan.analyzer.controller.BugRetriever;
import com.dissan.analyzer.controller.BugRetrieverAPI;
import com.dissan.analyzer.utils.OutController;

import java.io.IOException;

public class UserView {

    private static final String PROJECT = "BOOKKEEPER";

    /**
     * What the view should do
     * @param args args
     * @throws IOException exception
     */
    public static void main(String[] args) throws IOException {
        OutController out = new OutController();
        BugRetrieverAPI bugRetriever = new BugRetriever();
        bugRetriever.start();
        TicketJiraBean ticketJiraBean = bugRetriever.getJiraTicketBean(UserView.PROJECT);
        JiraBugBean bugBean = bugRetriever.getBugBean(UserView.PROJECT);
        out.println(ticketJiraBean.getBugReleases());
        out.println(bugBean.getBugVersionInfo());
    }
}
