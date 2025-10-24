package com.infinite_craft.networking;

import com.infinite_craft.InfiniteCraft;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SendArrowProgressS2CPayload(int progress) implements CustomPayload {
	public static final Identifier PAYLOAD_ID =
	Identifier.of(InfiniteCraft.MOD_ID, "set_arrow_progress");
	public static final Id<SendArrowProgressS2CPayload> ID = new Id<>(PAYLOAD_ID);

	public static final PacketCodec<RegistryByteBuf, SendArrowProgressS2CPayload> CODEC =
	PacketCodec.tuple(
	PacketCodecs.VAR_INT,                  // 编解码 int（兼容 RegistryByteBuf）
	SendArrowProgressS2CPayload::progress, // 提取字段
	SendArrowProgressS2CPayload::new       // 构造 record
	);

	@Override
	public Id<? extends CustomPayload> getId() {
	return ID;
	}
}
