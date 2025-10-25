package com.infinite_craft;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

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
  public static void initialize() {
  }
}
