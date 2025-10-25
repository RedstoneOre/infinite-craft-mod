package com.infinite_craft.ai;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.Schema;
import com.google.gson.JsonParser;

import net.minecraft.server.network.ServerPlayerEntity;

public class Gemini {
	private Client client = null;
	private static GenerateContentConfig config =
		GenerateContentConfig.builder()
			.responseMimeType("application/json")
			.responseSchema(
				Schema.fromJson(
					JsonParser.parseString(
						"""
						{
							"type": "object",
							"required": [
								"success",
								"itemNbt"
							],
							"properties": {
								"success": {
									"type": "boolean"
								},
								"itemNbt": {
									"type": "string"
								}
							}
						}
						"""
					).toString()
				)
			)
			.build();
	public Client getClient(ServerPlayerEntity player){
		return client;
	}
	public boolean initClient(String apiKey){
		if(client!=null) return false;
		client = new Client.Builder().apiKey(apiKey).build();
		return true;
	}
	public GenerateContentConfig getConfig(ServerPlayerEntity player){
		return config;
	}
}
