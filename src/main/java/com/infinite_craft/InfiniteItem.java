package com.infinite_craft;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import com.infinite_craft.element.catching.ElementCatcher;

import java.util.function.Function;

public class InfiniteItem{

	public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
		// Create the item key.
		RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(InfiniteCraft.MOD_ID, name));

		// Create the item instance.
		Item item = itemFactory.apply(settings.registryKey(itemKey));

		// Register the item.
		Registry.register(Registries.ITEM, itemKey, item);

		return item;
	}

	public static final Item CUSTOM_CRAFTED_ITEM = register("custom_crafted_item", Item::new, new Item.Settings());
	public static final Item VANILLAIFY = register("vanillaify", Item::new, new Item.Settings());
	public static final Item ELEMENT_CATCHER = register("element_catcher", ElementCatcher::new, new ElementCatcher.Settings());
	public static void initialize() {
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
			.register((itemGroup) -> itemGroup.add(VANILLAIFY));
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS)
			.register((itemGroup) -> itemGroup.add(ELEMENT_CATCHER));
	}
}
