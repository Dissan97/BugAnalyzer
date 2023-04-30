package com.dissan.analyzer.controller;

import com.dissan.analyzer.bean.JiraBugBean;
import com.dissan.analyzer.bean.TicketJiraBean;

import java.io.IOException;

public interface BugRetrieverAPI {
    void start() throws IOException;
    void close();
    TicketJiraBean getJiraTicketBean(String projectName);
    JiraBugBean getBugBean(String projectName);
}
