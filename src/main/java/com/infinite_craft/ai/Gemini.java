package com.infinite_craft.ai;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.Schema;
import com.infinite_craft.InfiniteCraft;
import com.infinite_craft.InfiniteCraftConfig;

import net.minecraft.server.network.ServerPlayerEntity;

public class Gemini {
	private Client client = null;
	private static GenerateContentConfig config =
		GenerateContentConfig.builder()
			.responseMimeType("application/json")
			.responseSchema(
				Schema.fromJson(AiPrompt.promptJsonSchema.toString())
			)
			.build();
	public Client getClient(ServerPlayerEntity player){
		return client;
	}
	public boolean initClient(InfiniteCraftConfig config){
		if(client!=null || config.GeminiApiKey == null) return false;
		if("system".equals(config.ModelProxy)){
			System.setProperty("java.net.useSystemProxies", "true");
			InfiniteCraft.LOGGER.info("Using System Proxies");

		}
		client = new Client.Builder()
			.apiKey(config.GeminiApiKey)
			.build();
		return true;
	}
	public GenerateContentConfig getConfig(ServerPlayerEntity player){
		return config;
	}
}
