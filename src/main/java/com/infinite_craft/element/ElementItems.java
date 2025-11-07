package com.infinite_craft.element;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

import com.infinite_craft.InfiniteCraft;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ElementItems {
	
	public static ElementItem registerElement(String name, Function<ElementItem.Settings, ElementItem> itemFactory, ElementItem.Settings settings) {
		// Create the item key.
		RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(InfiniteCraft.MOD_ID, name));

		// Create the item instance.
		ElementItem item = itemFactory.apply(settings.registryKey(itemKey));

		// Register the item.
		Registry.register(Registries.ITEM, itemKey, item);

		return item;
	}

	public static final ComponentType<ElementComponentType> ELEMENT_COMPONENT = Registry.register(
		Registries.DATA_COMPONENT_TYPE,
		Identifier.of(InfiniteCraft.MOD_ID, "element"),
		ComponentType.<ElementComponentType>builder().codec(ElementComponentType.CODEC).build()
	);
	public static final ElementItem ELEMENT = registerElement("element", ElementItem::new, new ElementItem.Settings());

	public static final ElementItem ELEMENT_WIND = registerElement("element.wind", ElementItem::new, new ElementItem.Settings()
		.component(ElementItems.ELEMENT_COMPONENT, 
			new ElementData("💨", "wind", "white").generateElementComponent()
		).modelId(Identifier.ofVanilla("wind_charge")));

	public static final ElementItem ELEMENT_FIRE = registerElement("element.fire", ElementItem::new, new ElementItem.Settings()
		.component(ElementItems.ELEMENT_COMPONENT, 
			new ElementData("🔥", "fire", "red").generateElementComponent()
		).modelId(Identifier.ofVanilla("flint_and_steel")));

	public static final ElementItem ELEMENT_WATER = registerElement("element.water", ElementItem::new, new ElementItem.Settings()
		.component(ElementItems.ELEMENT_COMPONENT, 
			new ElementData("💧", "water", "aqua").generateElementComponent()
		).modelId(Identifier.ofVanilla("water_bucket")));

	public static final ElementItem ELEMENT_EARTH = registerElement("element.earth", ElementItem::new, new ElementItem.Settings()
		.component(ElementItems.ELEMENT_COMPONENT, 
			new ElementData("🌍", "earth", "brown").generateElementComponent()
		).modelId(Identifier.ofVanilla("grass_block")));

	public static final Map<String, ? extends Item> BASIC_ELEMENTS = Map.of(
		"wind", ELEMENT_WIND,
		"fire", ELEMENT_FIRE,
		"water", ELEMENT_WATER,
		"earth", ELEMENT_EARTH
	);
	public static ItemStack generateElement(ElementData elementData, NbtCompound itemNbt){
		if(BASIC_ELEMENTS.containsKey(elementData.name)){
			try{
				ItemStack itemStack = BASIC_ELEMENTS.get(elementData.name).getDefaultStack();
				itemStack.setCount(1);
				return itemStack;
			}catch(Exception e){}

		}
		ItemStack itemStack = ELEMENT.getDefaultStack();
		itemStack.setCount(1);
		itemStack.set(ELEMENT_COMPONENT, elementData.generateElementComponent());
		try{
			itemStack.set(DataComponentTypes.ITEM_MODEL, Identifier.of(
				itemNbt.getCompoundOrEmpty("components").getString("minecraft:item_model").orElseGet(
					()->{
						return itemNbt.getCompoundOrEmpty("components").getString("item_model").orElseThrow();
					}
				)
			));
		} catch ( NoSuchElementException e ){
			e.printStackTrace();
		}
		InfiniteCraft.LOGGER.info("Generated Element: {}", ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, itemStack)
			.resultOrPartial(error -> {})
			.map(nbtElement -> (NbtCompound) nbtElement)
			.orElse(new NbtCompound())
			.toString()
		);
		return itemStack;
	}

	public static void initialize() {
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
			.register((itemGroup) -> {
				itemGroup.add(ELEMENT_WIND);
				itemGroup.add(ELEMENT_FIRE);
				itemGroup.add(ELEMENT_WATER);
				itemGroup.add(ELEMENT_EARTH);
			});
	}
}
