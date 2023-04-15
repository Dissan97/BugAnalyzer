package com.dissan.analyzer;

import org.json.JSONObject;
import org.json.JSONTokener;


import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ConfigProjectInitialization {

    private ConfigProjectInitialization(){}
    public static List<String> getProjectsToAnalyze(){
        List<String> projectsToAnalyze = new ArrayList<>();
        String file = "conf.json";
        JSONObject jsonObject;


        try (InputStream inputStream = ConfigProjectInitialization.class.getResourceAsStream(file)){
            assert inputStream != null;
            JSONTokener tokenizer = new JSONTokener(inputStream);
            jsonObject = new JSONObject(tokenizer);

            Set<String> keys =  jsonObject.keySet();

            for (String key : keys){
                projectsToAnalyze.add(jsonObject.get(key).toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return projectsToAnalyze;
    }

}
