package com.infinite_craft;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.infinite_craft.ai.Gemini;
import com.infinite_craft.element.DiscoveringPlayerData;
import com.infinite_craft.element.ElementItems;
import com.infinite_craft.element.globaldata.GlobalDiscoveringData;
import com.infinite_craft.element.globaldata.GlobalDiscoveringDataManager;
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
		ElementItems.initialize();
		PayloadTypeRegistry.playS2C().register(SendArrowProgressS2CPayload.ID, SendArrowProgressS2CPayload.CODEC);
		InfiniteCraftFakeProgressTask.registerTickHandler();
		config.load();
		config.write();
		gemini.initClient(config);
		DiscoveringPlayerData.register();

		
        // ✅ 监听服务器启动事件
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            GlobalDiscoveringData globalData = GlobalDiscoveringDataManager.get(server);
            LOGGER.info("[InfiniteCraft] Loaded global discovered_elements data with {} entries.",
                globalData.getDiscovered().getData().size());
        });

		// ✅ 世界保存时自动保存 GlobalDiscoveringData
        ServerLifecycleEvents.BEFORE_SAVE.register((server, flush, force) -> {
			GlobalDiscoveringDataManager.markDirty(server);
			LOGGER.info("[InfiniteCraft] Auto-saved GlobalDiscoveringData.");
        });

        // ✅ 服务器停止时自动保存（Fabric 会自动写入 markDirty 的 PersistentState）
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            LOGGER.info("[InfiniteCraft] Saving discovered_elements...");
            GlobalDiscoveringDataManager.markDirty(server);
        });
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