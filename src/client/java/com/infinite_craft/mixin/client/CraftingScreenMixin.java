package com.infinite_craft.mixin.client;

import com.infinite_craft.ICraftingScreen;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 当无法合成时，在客户端结果栏显示 infinite_craft:custom_crafted_item。
 * 增加：箭头进度条显示，由服务器控制。
 */
@Mixin(CraftingScreen.class)
public abstract class CraftingScreenMixin implements ICraftingScreen {

	// 当前箭头进度 0~100，由服务器更新
	private static int arrowProgress = 0;

	@Shadow private static Identifier TEXTURE;

	// 允许网络包更新进度
    @Override
	public void setArrowProgress(int progress) {
        arrowProgress=progress;
	}

	// 原有自定义输出注入
	@Inject(method = "drawBackground(Lnet/minecraft/client/gui/DrawContext;FII)V", at = @At("TAIL"))
	private void injectCustomOutputItem(DrawContext context, float delta, int mouseX, int mouseY, CallbackInfo ci) {
        CraftingScreen screen = (CraftingScreen)(Object)this;
        CraftingScreenHandler handler = screen.getScreenHandler();

        if (handler == null || handler.getSlot(0) == null) return;

        boolean hasInput = false;
            for (int i = 1; i <= 9; i++) {
            if (!handler.getSlot(i).getStack().isEmpty()) {
                hasInput = true;
                break;
            }
        }

        ItemStack output = handler.getSlot(0).getStack();
        if (output.isEmpty() && hasInput) {
            Item customItem = Registries.ITEM.get(Identifier.of("infinite_craft", "custom_crafted_item"));
            if (customItem != null) {
                handler.getSlot(0).setStack(new ItemStack(customItem));
            }
        }

        // 绘制箭头进度条
        if (arrowProgress > 0) {
            HandledScreenAccessor accessor = (HandledScreenAccessor) (Object) this;
            int x = accessor.getX();
            int y = accessor.getY();
            int arrowWidth = (int) (arrowProgress / 100.0f * 24);

            context.drawGuiTexture(
                RenderPipelines.GUI_TEXTURED,
                Identifier.ofVanilla("container/furnace/burn_progress"),
                24, 16,
                0, 0,
                x + 89, y + 34,
                arrowWidth, 16
            );
        }
	}
}

