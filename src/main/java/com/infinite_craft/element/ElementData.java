package com.infinite_craft.element;

import java.util.NoSuchElementException;
import java.util.Optional;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class ElementData {
	String name;
	String emoji;
	String color;
    Identifier model;
    Optional<String> translated;

    private void init(String emoji, String name, String color, Identifier model, Optional<String> translated) throws NullPointerException {
        if(emoji == null || name == null || color == null || model == null || translated == null){
            throw new NullPointerException("Null string when constructing ElementData");
        }
        this.emoji = emoji;
        this.name = toTitleCase(name);
        this.color = color;
        this.model = model;
        translated.ifPresentOrElse(
            str -> {this.translated=Optional.of(toTitleCase(str));},
            () -> {this.translated=Optional.empty();}
        );
    }

    public ElementData(String emoji, String name, String color, Identifier model) throws NullPointerException {
        init(emoji, name, color, model, Optional.empty());
    }
    public ElementData(String emoji, String name, String color, Identifier model, String translated) throws NullPointerException {
        init(emoji, name, color, model, Optional.ofNullable(translated));
    }
    
    /**
     * @throws NoSuchElementException model is not an {@link net.minecraft.util.Identifier Identifier}
     */
    public ElementData(NbtCompound nbtCompound) throws NullPointerException, NoSuchElementException {
        if(!nbtCompound.getString("name").isPresent()
        || !nbtCompound.getString("emoji").isPresent()
        || !nbtCompound.getString("color").isPresent()
        || !nbtCompound.getString("model").isPresent()){
            throw new NullPointerException("Illegal nbt compound when constructing ElementData");
        }
        init(
            nbtCompound.getString("name").get(),
            nbtCompound.getString("emoji").get(),
            nbtCompound.getString("color").get(),
            Optional.ofNullable(Identifier.tryParse(nbtCompound.getString("model").get())).orElseThrow(),
            nbtCompound.getString("translated")
        );
    }

    public ElementData(ElementComponentType componentType) throws NullPointerException {
        init(componentType.emoji(), componentType.name(), componentType.color(), componentType.model(), componentType.translated());
    }

	public ElementComponentType generateElementComponent(){
		return new ElementComponentType(emoji, name, color, model, translated);
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
        return String.format("%s%s", emoji, name);
    }

    public static String toTitleCase(String input) {
        StringBuilder result = new StringBuilder();
        StringBuilder currentWord = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (c == '_') {
                appendWord(result, currentWord);
                continue;
            }

            currentWord.append(c);
        }

        // Last Word
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

    public ElementData checked(DiscoveringPlayerData checkingData){
        checkingData.get(name).ifPresent(value -> {
            this.init(value.emoji(), value.name(), value.color(), value.model(), value.translated());
        });
        return this;
    }

    public Identifier getModel() {
        return model;
    }

    public Optional<String> getTranslated() {
        return translated;
    }
}