package com.dissan.analyzer.api;



import com.dissan.analyzer.system.JiraTicketBean;
import com.dissan.analyzer.system.TicketJiraBean;

import java.io.IOException;

public interface BugRetrieverAPI {
    void start() throws IOException;
    void close();
    TicketJiraBean getJiraTicketBean(String projectName);
    JiraTicketBean getBugBean(String projectName);
}
