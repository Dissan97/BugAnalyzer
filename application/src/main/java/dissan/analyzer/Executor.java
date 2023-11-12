package dissan.analyzer;

import com.dissan.analyzer.AnalyzerFactory;
import com.dissan.analyzer.api.BugRetrieverAPI;

import java.io.IOException;

public class Executor {

    public static void main(String[] args) throws IOException {
        BugRetrieverAPI controller = AnalyzerFactory.getBugRetriever();
    }
}
