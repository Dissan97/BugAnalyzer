package com.dissan.analyzer;

import com.dissan.analyzer.api.BugRetrieverAPI;
import com.dissan.analyzer.system.BugRetriever;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class AnalyzerFactory {

    private AnalyzerFactory(){}

    public static @NotNull BugRetrieverAPI getBugRetriever() throws IOException {
        BugRetriever bugRetriever = new BugRetriever();
        bugRetriever.start();
        return bugRetriever;
    }
}
