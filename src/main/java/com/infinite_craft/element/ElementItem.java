package com.infinite_craft.element;

import java.util.Optional;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

public class ElementItem extends Item {

	@Override
	public Text getName(ItemStack stack) {
		ElementComponentType elementData = stack.getComponents().get(ElementItems.ELEMENT_COMPONENT);
		Text elementText = elementData == null ?
			stack.getComponents().getOrDefault(DataComponentTypes.ITEM_NAME, ScreenTexts.EMPTY) :
			Text.literal(elementData.emoji()+elementData.translated().orElseGet(elementData::name)).withColor(TextColor.parse(elementData.color()).result().orElse(TextColor.fromRgb(0xffffff)).getRgb());
		return elementText;
	}

	public ElementItem(Settings settings) {
		super(settings);
	}
}
