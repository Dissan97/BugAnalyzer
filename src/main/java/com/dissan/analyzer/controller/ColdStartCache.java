package com.dissan.analyzer.controller;

import com.dissan.analyzer.bean.JiraBugBean;
import com.dissan.analyzer.bean.TicketJiraBean;
import com.dissan.analyzer.model.JiraBug;
import com.dissan.analyzer.model.Release;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

public class ColdStartCache {
    Map<String, JiraBugBean> otherProjectsMap = new HashMap<>();
    public final static String CONF = "coldStartQueries.json";
    private final static String CACHE = "coldStartCache.json";
    public Map<String, JiraBugBean> getProjectsMap() {
        this.otherProjectsMap = new HashMap<>();
        Set<String> keys;
        try {
            InputStream reader = (Objects.requireNonNull(this.getClass().getResourceAsStream(CACHE)));
            JSONTokener jsonTokener = new JSONTokener(reader);
            JSONObject jsonObject = new JSONObject(jsonTokener);
             keys = jsonObject.keySet();
             if (keys.size() < 5){
                 throw new JSONException("Missing args");
             }



        }catch (NullPointerException | JSONException e){
            //todo when the file does not exist
            Logger logger = Logger.getLogger(this.getClass().getSimpleName());
            logger.info(e.getMessage());
            setUpCache();
        }
        return this.otherProjectsMap;
    }


    private void setUpCache(){
        BugRetriever bugRetriever = new BugRetriever();
        //todo add bufferedWriter to create json file...
        String path = Objects.requireNonNull(this.getClass().getResource("")).getPath();
        path += CACHE;

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            bugRetriever.start(ColdStartCache.CONF);
            this.otherProjectsMap = bugRetriever.getBugMap();
            Set<String> keys = this.otherProjectsMap.keySet();
            int projects = 0;
            writer.write("{\n");
            for (String k:
                    keys) {
                JiraBugBean beanAPI = this.otherProjectsMap.get(k);
                List<JiraBug> bugBeans = beanAPI.getBugWithIv();
                JiraBugBean bugBean = new JiraBugBean(k, bugBeans, bugRetriever.getJiraTicketBean(k));
                if (projects < keys.size() - 1){
                    System.out.println(bugBean.getReleases());
                    writer.write(bugBean.getReleases()+",");
                }else {
                    writer.write((bugBean.getReleases()));
                }
                projects ++;
            }
            writer.write('}');
            writer.flush();
        } catch (IOException e) {
            //todo remove this
            e.printStackTrace();
        }

    }
}
