package com.infinite_craft.process;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.infinite_craft.InfiniteCraft;

import net.minecraft.server.network.ServerPlayerEntity;

public class AiApi {
	
	public static JsonObject doPost(String prompt, ServerPlayerEntity player) throws IOException, RuntimeException {
		return doPostGemini(prompt, player);
	};

    public static JsonObject doPostGemini(String prompt, ServerPlayerEntity player) throws IOException, RuntimeException {
		try {
			Client geminiClient = InfiniteCraft.gemini.getClient(player);
			if(geminiClient==null){
				throw new RuntimeException("Gemini is not initialized");
			}
			GenerateContentResponse response =
				geminiClient.models.generateContent(
					"gemini-2.5-flash",
					prompt,
					InfiniteCraft.gemini.getConfig(player)
				);
			return JsonParser.parseString(response.text()).getAsJsonObject();
		} catch( Exception e ){
			e.printStackTrace();
			throw e;
		}
    }
    public static JsonObject doPostOllama(String prompt) throws IOException, RuntimeException {
		String urlStr="http://localhost:11434/api/generate";
        // if(true){JsonObject tmp = new JsonObject();tmp.addProperty("success", true);tmp.addProperty("itemNbt", "{\"id\":\"minecraft:diamond\",\"count\":1,\"components\":{\"enchantments\":{\"sharpness\":2}}}");JsonObject response = new JsonObject();response.addProperty("response", tmp.toString());return response;}
        URL url = URI.create(urlStr).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

		JsonObject body = JsonParser.parseString("""
			{
				"model": "deepseek-r1:8b",
				"format": {
					"type": "object",
					"properties": {
						"itemNbt": {"type": "string"},
						"success": {"type": "boolean"}
					},
					"required": ["itemNbt", "success"]
				},
				"stream": false
			}
			""").getAsJsonObject();
		body.addProperty("prompt", prompt);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.toString().getBytes(StandardCharsets.UTF_8));
        }

        try (InputStream is = conn.getInputStream();
             InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
			try{
				return JsonParser.parseString(
					JsonParser.parseReader(reader)
						.getAsJsonObject()
						.get("response")
						.getAsString()
				).getAsJsonObject();
			} catch(Exception e){
				throw new RuntimeException("The AI api is kinda broken,Unable to Get Response!\n"+e.getMessage());
			}
        }
    }
}
