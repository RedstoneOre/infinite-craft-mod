package com.infinite_craft.mixin.client;

import net.minecraft.client.item.ItemModelManager;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.infinite_craft.element.ElementData;

@Mixin(ItemModelManager.class)
public class ItemModelManagerMixin {
    /**
     * Modify the ItemStack parameter in update() before any logic runs.
     */
    @ModifyVariable(
        method = "update",
        at = @At("HEAD"),
        argsOnly = true
    )
    private ItemStack modifyUpdateStack(ItemStack original) {
        if (ElementData.isElement(original)) {
            ItemStack stack = original.copy();
            ElementData.fromItem(stack).ifPresent(element ->
                stack.set(DataComponentTypes.ITEM_MODEL, element.getModel())
            );
            return stack;
        }
        return original;
    }

    /**
     * Modify the ItemStack parameter in hasHandAnimationOnSwap() before any logic runs.
     */
    @ModifyVariable(
        method = "hasHandAnimationOnSwap",
        at = @At("HEAD"),
        argsOnly = true
    )
    private ItemStack modifySwapStack(ItemStack original) {
        if (ElementData.isElement(original)) {
            ItemStack stack = original.copy();
            ElementData.fromItem(stack).ifPresent(element ->
                stack.set(DataComponentTypes.ITEM_MODEL, element.getModel())
            );
            return stack;
        }
        return original;
    }
}
