package com.infinite_craft.mixin;

import com.infinite_craft.process.InfiniteCraftFakeProgressTask;
import com.infinite_craft.networking.InfiniteCraftNetworking;
import com.infinite_craft.InfiniteCraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(CraftingScreenHandler.class)
public class CraftingScreenHandlerMixin {
	
	@Inject(method = "<init>", at = @At("TAIL"))
    private void onConstruct(int syncId, PlayerInventory inventory, CallbackInfo ci) {
		InfiniteCraft.LOGGER.info("CraftingScreenHandlerMixin triggered for {}", inventory.player.getName().getString());

		if( inventory.player instanceof ServerPlayerEntity serverPlayer ){
			InfiniteCraftNetworking.sendArrowProgress(serverPlayer, (int) InfiniteCraftFakeProgressTask.getProgress(serverPlayer));
		}
    }
}
