package com.infinite_craft;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

public class InfiniteCraftConfig {

    public String GeminiApiKey = null;

    public void load() {
        File configFile = new File("config/InfiniteCraft.json");
        if (!configFile.exists()) {
            System.err.println("Config file not found: " + configFile.getAbsolutePath());
            try{
                configFile.createNewFile();
                if(configFile.canWrite()){
                    FileWriter writer = new FileWriter(configFile);
                    writer.append("""
                            {
                                "GeminiApiKey": null
                            }
                            """);
                    writer.close();
                }
            } catch ( Exception e ){
                e.printStackTrace();
            }
            return;
        }

        try (FileReader reader = new FileReader(configFile)) {
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(reader, JsonObject.class);
            GeminiApiKey = (json.has("GeminiApiKey") && json.get("GeminiApiKey").isJsonPrimitive() && json.get("GeminiApiKey").getAsJsonPrimitive().isString()) ? json.get("GeminiApiKey").getAsString() : null;
            InfiniteCraft.LOGGER.info("Google Api Key: "+(GeminiApiKey!=null? GeminiApiKey : "Failed to Get"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}