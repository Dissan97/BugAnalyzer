package com.dissan.analyzer.utils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigProjectsJson {

    private static final Logger LOGGER = Logger.getLogger(ConfigProjectsJson.class.getSimpleName());

    private final List<JiraApiRequest> apiRequests = new ArrayList<>();
    public ConfigProjectsJson(String confFile) {
        InputStream reader = (Objects.requireNonNull(this.getClass().getResourceAsStream(confFile)));
        JSONTokener jsonTokener = new JSONTokener(reader);
        JSONObject jsonObject = new JSONObject(jsonTokener);
        Set<String> projects = jsonObject.keySet();
        for (String p:
             projects) {
            String field = getFields(jsonObject.getJSONObject(p));
            JSONObject innerFields = jsonObject.getJSONObject(p);
            int start = innerFields.getInt("start");
            apiRequests.add(new JiraApiRequest(p, field, start));
        }
    }

    private String getFields(@NotNull JSONObject jsonObject) {
        StringBuilder retVal = new StringBuilder();
        try {
            JSONArray fields = jsonObject.getJSONArray("fields");
            int length = fields.length() - 1;
            retVal.append("%20&fields=");
            for (int i = 0; i < length; i++){
                retVal.append(fields.getString(i)).append(",");
            }
            retVal.append(fields.getString(length));
        }catch (JSONException e){
            LOGGER.log(Level.WARNING, e.getMessage());
        }
        return retVal.toString();
    }

    public List<JiraApiRequest> getApiRequests() {
        return apiRequests;
    }




}
