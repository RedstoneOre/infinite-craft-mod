package com.infinite_craft.element;

import java.util.Optional;

import com.mojang.serialization.Codec;

import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;

public class ElementData {
	String name;
	String emoji;
	String color;

    private void init(String emoji, String name, String color) {
        if(emoji == null || name == null){
            throw new NullPointerException("Null string when constructing ElementData");
        }
        this.emoji = emoji;
        this.name = name;
        this.color = color;
    }

    public ElementData(String emoji, String name, String color){
        init(emoji, name, color);
    }
    
    public ElementData(NbtCompound nbtCompound) {
        if(!nbtCompound.getString("name").isPresent()
        || !nbtCompound.getString("emoji").isPresent()
        || !nbtCompound.getString("color").isPresent()){
            throw new NullPointerException("Illegal nbt compound when constructing ElementData");
        }
        init(
            nbtCompound.getString("name").get(),
            nbtCompound.getString("emoji").get(),
            nbtCompound.getString("color").get()
        );
    }

    public ElementData(ElementComponentType componentType){
        init(componentType.emoji(), componentType.name(), componentType.color());
    }

	public ElementComponentType generateElementComponent(){
		NbtCompound result = new NbtCompound();
		result.put("name", Codec.STRING, name);
		result.put("emoji", Codec.STRING, emoji);
		result.put("color", Codec.STRING, color);
		return ElementComponentType.from(result);
	}

	public static boolean isElement(Item item){
		return item.getComponents().contains(ElementItems.ELEMENT_COMPONENT);
	}
    public static Optional<ElementData> fromItem(Item item){
        if(!isElement(item)){
            return Optional.empty();
        }
        return Optional.of(new ElementData(item.getComponents().get(ElementItems.ELEMENT_COMPONENT)));
    }

    public String getName() {
        return name;
    }

    public String getEmoji() {
        return emoji;
    }

    public String getColor() {
        return color;
    }

    @Override
    public String toString(){
        return String.format("%s %s", emoji, name);
    }
}