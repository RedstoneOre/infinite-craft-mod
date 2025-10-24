package com.infinite_craft.networking;

import com.infinite_craft.InfiniteCraftClient;
import com.infinite_craft.ICraftingScreen;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;

public class InfiniteCraftClientNetworking {

	public static void register() {
		ClientPlayNetworking.registerGlobalReceiver(
			SendArrowProgressS2CPayload.ID,
			(payload, context) -> {
				// 这里 payload 已经是 SendArrowProgressS2CPayload 类型
				int progress = payload.progress();

				context.client().execute(() -> {
					InfiniteCraftClient.LOGGER.info("收到箭头进度: {}", progress);
					if (MinecraftClient.getInstance().currentScreen instanceof CraftingScreen screen) {
						((ICraftingScreen) screen).setArrowProgress(progress);
					}
				});
			}
		);
	}
}
