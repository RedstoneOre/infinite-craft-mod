package com.infinite_craft;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.util.Set;


public class InfiniteCraftConfig {

    public String GeminiApiKey = null;
    public String GeminiModelName = null;
    public String ModelProxy = "none";
    public String ModelType = "none";
    public final JsonObject defaultConfig = JsonParser.parseString(
        """
        {
            "gemini": {
                "api_key": null,
                "model": "",
                "note": [
                    "Gemini API Is Currently Free!",
                    "the `model` can be: ",
                    "    gemini-2.5-flash",
                    "    gemini-2.5-flash-lite",
                    "    or any other ones in https://ai.google.dev/gemini-api/docs/models",
                    "For API key, generate here: https://aistudio.google.com/api-keys"
                ]
            },
            "proxy": "none",
            "model": "gemini",
            "description": {
                "proxy": "Proxy type, could be (none, system)",
                "model": "The model to use, could be (none, gemini, ollama), use none if you only use the mod as a player of a server",
                "gemini": "The gemini config",
                "!!!": "This file will be overridden, so please save your important information somewhere else!"
            }
        }
        """
    ).getAsJsonObject();

    private static class KeyNames {
        public static final String GeminiJson="gemini";
        public static final String GeminiApiKey="api_key";
        public static final String GeminiModelName="model";
        public static final StringOptions Proxy = new StringOptions("proxy", Set.of("none", "system"), "none");
        public static final StringOptions ModelType = new StringOptions("model", Set.of("none", "gemini", "ollama"), "none");
        public static class StringOptions {
            public String name;
            public String defaultValue;
            public Set<String> options;
            StringOptions(String _name, Set<String> _options, String _default) {
                name=_name;
                defaultValue=_default;
                options=_options;
            }
            public String get(String ori){
                if(options.contains(ori)){
                    return ori;
                }
                return defaultValue;
            }
        }

    }
    public void load() {
        File configFile = getConfigFile();
        if (!configFile.exists()) {
            createFile(configFile);
        }

        try (FileReader reader = new FileReader(configFile)) {
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(reader, JsonObject.class);
            if(json.has(KeyNames.GeminiJson) && json.get(KeyNames.GeminiJson).isJsonObject()){
                JsonObject gemeniConfig = json.get(KeyNames.GeminiJson).getAsJsonObject();
                GeminiApiKey=readStringFromObject(gemeniConfig, KeyNames.GeminiApiKey);
                GeminiModelName=readStringFromObject(gemeniConfig, KeyNames.GeminiModelName);
                if(GeminiModelName==null) GeminiModelName="gemini-2.5-flash";
            }
            ModelProxy=KeyNames.Proxy.get(readStringFromObject(json, KeyNames.Proxy.name));
            ModelType=KeyNames.ModelType.get(readStringFromObject(json, KeyNames.ModelType.name));
        } catch (IOException e) {
            InfiniteCraft.LOGGER.error(e.getMessage());
        }
    }

    private String readStringFromObject(JsonObject object, String key){
        return (object.has(key) && object.get(key).isJsonPrimitive() && object.get(key).getAsJsonPrimitive().isString()) ? object.get(key).getAsString() : null;
    }

    public void write(){
        File configFile = getConfigFile();
        if (!configFile.exists()) {
            createFile(configFile);
            return;
        }

        JsonObject newConfig = defaultConfig;
        try{
            FileWriter writer = new FileWriter(configFile);
            try {
                newConfig.addProperty(KeyNames.Proxy.name, ModelProxy);
                newConfig.addProperty(KeyNames.ModelType.name, ModelType);

                JsonObject geminiConfig = newConfig.get(KeyNames.GeminiJson).getAsJsonObject();
                geminiConfig.addProperty(KeyNames.GeminiApiKey, GeminiApiKey);
                geminiConfig.addProperty(KeyNames.GeminiModelName, GeminiModelName);
            } catch (Exception e) {
                System.err.print(e.getMessage());
            }
            writer.append(new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create()
                .toJson(newConfig)
            );
            writer.close();
        } catch (IOException e) {
            InfiniteCraft.LOGGER.error(e.getMessage());
        }
    }

    private void createFile(File configFile){
        try{
            configFile.createNewFile();
            if(configFile.canWrite()){
                FileWriter writer = new FileWriter(configFile);
                writer.append(new GsonBuilder()
                    .setPrettyPrinting()
                    .serializeNulls()
                    .create()
                    .toJson(defaultConfig)
                );
                writer.close();
            }
        } catch ( Exception e ){
            InfiniteCraft.LOGGER.error(e.getMessage());
        }
    }

    private File getConfigFile(){
        return new File("config/InfiniteCraft.json");
    }
}