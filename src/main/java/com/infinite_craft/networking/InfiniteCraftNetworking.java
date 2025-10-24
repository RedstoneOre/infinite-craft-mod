package com.infinite_craft.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

public class InfiniteCraftNetworking {

	/**
	 * 发送工作台箭头进度给客户端
	 * @param player 目标玩家（必须是ServerPlayerEntity）
	 * @param progress 0~100的百分比
	 */
	public static void sendArrowProgress(ServerPlayerEntity player, int progress) {
	ServerPlayNetworking.send(player, new SendArrowProgressS2CPayload(progress));
	}
}