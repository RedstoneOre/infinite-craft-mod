package com.infinite_craft.element;

import java.util.Optional;

import com.mojang.serialization.Codec;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
        this.name = toTitleCase(name);
        this.color = color;
    }

    public ElementData(String emoji, String name, String color) throws NullPointerException {
        init(emoji, name, color);
    }
    
    public ElementData(NbtCompound nbtCompound) throws NullPointerException {
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

    public ElementData(ElementComponentType componentType) throws NullPointerException {
        init(componentType.emoji(), componentType.name(), componentType.color());
    }

	public ElementComponentType generateElementComponent(){
		NbtCompound result = new NbtCompound();
		result.put("name", Codec.STRING, name);
		result.put("emoji", Codec.STRING, emoji);
		result.put("color", Codec.STRING, color);
		return ElementComponentType.from(result);
	}

	public static boolean isElement(ItemStack itemStack){
		return itemStack.getComponents().contains(ElementItems.ELEMENT_COMPONENT);
	}
    public static Optional<ElementData> fromItem(ItemStack itemStack){
        if(!isElement(itemStack)){
            return Optional.empty();
        }
        return Optional.of(new ElementData(itemStack.getComponents().get(ElementItems.ELEMENT_COMPONENT)));
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

    public static String toTitleCase(String input) {
        StringBuilder result = new StringBuilder();
        StringBuilder currentWord = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            // 下划线表示新单词
            if (c == '_') {
                appendWord(result, currentWord);
                continue;
            }

            currentWord.append(c);
        }

        // 添加最后一个词
        appendWord(result, currentWord);

        return result.toString().trim();
    }

    private static void appendWord(StringBuilder result, StringBuilder word) {
        if (word.length() == 0) return;

        String w = word.toString();
        result.append(Character.toUpperCase(w.charAt(0)));
        if (w.length() > 1) {
            result.append(w.substring(1));
        }
        result.append(" ");
        word.setLength(0);
    }

}