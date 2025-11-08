package com.infinite_craft.mixin;

import com.infinite_craft.InfiniteCraft;

import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.infinite_craft.networking.InfiniteCraftNetworking;
import com.infinite_craft.process.InfiniteCraftProcess;

/**
 * 通用拦截：在 ScreenHandler 层注入。
 * 当玩家点击工作台输出栏但配方无效时触发事件。
 */
@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {

	@Inject(
		method = "onSlotClick",
		at = @At("HEAD")
	)
	private void onInvalidCraftClick(
		int slotIndex, int button, SlotActionType actionType, PlayerEntity player,
		CallbackInfo ci // 使用 CallbackInfo，因为 onSlotClick 返回 void
	) {
		// 将当前 ScreenHandler 转换为对象
		ScreenHandler self = (ScreenHandler) (Object) this;

		// 只处理 CraftingScreenHandler
		if (!(self instanceof CraftingScreenHandler handler)) {
			return;
		}

		// 获取世界对象
		World world = player.getEntityWorld();

		// 仅服务器端处理
		if (world.isClient()) {
			return;
		}

		// 只处理输出槽点击（index 0）
		if (slotIndex == 0) {
		Slot outputSlot = handler.getSlot(0);
		ItemStack output = outputSlot.getStack();

		// 当输出格为空时，记录输入格物品
		if (output.isEmpty()) {
				ItemStack[] inputs = new ItemStack[9];
				for (int i = 1; i <= 9; i++) {
				inputs[i - 1] = handler.getSlot(i).getStack().copy();
				}

				// 打日志
				InfiniteCraft.LOGGER.info("⚙️ [Infinite Craft] player {} triggered Infinite Crafting!", player.getName().getString());
				for (ItemStack stack : inputs) {
					if (!stack.isEmpty()) {
						InfiniteCraft.LOGGER.info("- {}x {}", stack.getCount(), stack.getItem());
					}
				}

				if (player instanceof ServerPlayerEntity serverPlayer) {
					InfiniteCraftNetworking.sendArrowProgress(serverPlayer, 0);
					try{
						InfiniteCraftProcess.requestCraftResult(world.getServer(), serverPlayer, serverPlayer.getBlockPos(), serverPlayer.getEntityWorld(), handler);
					} catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}
	}
}
