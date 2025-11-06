package com.infinite_craft.element;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.nbt.NbtCompound;

public record ElementComponentType(String emoji, String name, String color) {
    public static final Codec<ElementComponentType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.fieldOf("color").forGetter(ElementComponentType::color),
        Codec.STRING.fieldOf("name").forGetter(ElementComponentType::name),
        Codec.STRING.fieldOf("emoji").forGetter(ElementComponentType::emoji)
    ).apply(instance, ElementComponentType::new));

    public static ElementComponentType from(NbtCompound nbt) {
        String emoji = nbt.getString("emoji").orElseThrow();
        String name = nbt.getString("name").orElseThrow();
        String color = nbt.getString("color").orElseThrow();
        return new ElementComponentType(emoji, name, color);
    }
}
