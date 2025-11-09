package com.infinite_craft.mixin;

import com.infinite_craft.process.InfiniteCraftFakeProgressTask;
import com.infinite_craft.networking.InfiniteCraftNetworking;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(CraftingTableBlock.class)
public class CraftingTableBlockMixin {
    @Inject(
        method = "onUse(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;",
        at = @At("HEAD")
    )
    private void onCraftingTableUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (!world.isClient() && player instanceof ServerPlayerEntity serverPlayer && !hit.isAgainstWorldBorder()) {
			InfiniteCraftNetworking.sendArrowProgress(serverPlayer, (int) InfiniteCraftFakeProgressTask.getProgress(serverPlayer));
		}
    }
}