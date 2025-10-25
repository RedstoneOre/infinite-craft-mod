package com.infinite_craft;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.infinite_craft.ai.Gemini;
import com.infinite_craft.networking.SendArrowProgressS2CPayload;
import com.infinite_craft.process.InfiniteCraftFakeProgressTask;
// import com.openai.client.OpenAIClient;
// import com.openai.client.okhttp.OpenAIOkHttpClient;
// import com.openai.models.responses.ResponseCreateParams;

public class InfiniteCraft implements ModInitializer {
	public static final String MOD_ID = "infinite_craft";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// OpenAIClient openAIClient = null;
	public static Gemini gemini = new Gemini();

	public static InfiniteCraftConfig config = new InfiniteCraftConfig();

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		InfiniteItem.initialize();
		PayloadTypeRegistry.playS2C().register(SendArrowProgressS2CPayload.ID, SendArrowProgressS2CPayload.CODEC);
		InfiniteCraftFakeProgressTask.registerTickHandler();
		config.load();
		gemini.initClient(config.GeminiApiKey);
	}
	public InfiniteCraft(){
		super();
	}

	/**
	 * 没钱搞
	 * @param apiKey - a key
	 * @deprecated
	 */
	@Deprecated
	public void InitializeOpenAIClient(String apiKey){
		// openAIClient=OpenAIOkHttpClient.builder().apiKey(apiKey).build();
	}
}