package com.infinite_craft.element.catching;

import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.sound.SoundCategory;

import com.infinite_craft.element.ElementItems;

import java.util.function.Consumer;

import com.infinite_craft.InfiniteCraft;

/**
 * ElementCatcher
 * When been right-clicked, search in 5×7×5 range centered the player，
 * then consume itself and create the element。
 */
public class ElementCatcher extends Item {

    public ElementCatcher(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return this.use(context.getWorld(), context.getPlayer(), context.getHand());
    }

    @Override
    public ActionResult use(World world, net.minecraft.entity.player.PlayerEntity player, Hand hand) {
        if (world.isClient() || player == null) {
            return ActionResult.SUCCESS;
        }

        ItemStack stack = player.getStackInHand(hand);
        BlockPos center = player.getBlockPos();

        int radiusX = 2;
        int radiusY = 3;
        int radiusZ = 2;
        long totalBlocks = 1L * (radiusX*2+1) * (radiusY*2+1) * (radiusZ*2+1);

        int totalAir = 0;
        int totalFire = 0;
        int totalWater = 0;
        int totalNatural = 0;

        for (int dx = -radiusX; dx <= radiusX; dx++) {
            for (int dy = -radiusY; dy <= radiusY; dy++) {
                for (int dz = -radiusZ; dz <= radiusZ; dz++) {
                    BlockPos pos = center.add(dx, dy, dz);
                    if (world.isAir(pos)) {
                        ++totalAir;
                    }
                    if (world.getBlockState(pos).isIn(TagKey.of(RegistryKeys.BLOCK, BLOCK_TAG_EARTH))){
                        ++totalNatural;
                    }
                    if (world.getBlockState(pos).isIn(TagKey.of(RegistryKeys.BLOCK, BLOCK_TAG_FIRE))){
                        ++totalFire;
                    }
                    if (world.getBlockState(pos).isIn(TagKey.of(RegistryKeys.BLOCK, BLOCK_TAG_WATER))){
                        ++totalWater;
                    }
                }
            }
        }
        ItemStack reward = ItemStack.EMPTY;

        totalNatural/=2;
        if(totalAir*10 >= totalBlocks*9 || Math.max(totalNatural, Math.max(totalFire, totalWater)) < totalBlocks/10+1) {
            reward=ElementItems.ELEMENT_WIND.getDefaultStack();
        } else if(totalNatural/3 >= Math.max(totalFire, totalWater)){
            reward=ElementItems.ELEMENT_EARTH.getDefaultStack();
        } else if(totalFire >= totalWater) {
            reward=ElementItems.ELEMENT_FIRE.getDefaultStack();
        } else {
            reward=ElementItems.ELEMENT_WATER.getDefaultStack();
        }
        reward.setCount(1);

        // Sound Effect
        world.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE,
                SoundCategory.PLAYERS,
                1.0F,
                1.0F
        );

        // Consume 1 item
        stack.decrement(1);

        // Reward
        if (!player.getInventory().insertStack(reward)) {
            player.dropItem(reward, false);
        }

        return ActionResult.SUCCESS;
    }

    @Override
    @SuppressWarnings("deprecation")
	public void appendTooltip(
		ItemStack stack, Item.TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type
	) {
        textConsumer.accept(Text.translatable(stack.getItem().getTranslationKey()+".tooltip").withColor(
            TextColor.parse("gray").resultOrPartial().orElse(TextColor.fromRgb(0xafafaf)).getRgb()
        ));
        textConsumer.accept(Text.translatable(stack.getItem().getTranslationKey()+".tooltip.2").withColor(
            TextColor.parse("gray").resultOrPartial().orElse(TextColor.fromRgb(0xafafaf)).getRgb()
        ));
	}

    private static final Identifier BLOCK_TAG_EARTH = Identifier.of(InfiniteCraft.MOD_ID, "element_blocks/earth");
    private static final Identifier BLOCK_TAG_FIRE = Identifier.of(InfiniteCraft.MOD_ID, "element_blocks/fire");
    private static final Identifier BLOCK_TAG_WATER = Identifier.of(InfiniteCraft.MOD_ID, "element_blocks/water");

}