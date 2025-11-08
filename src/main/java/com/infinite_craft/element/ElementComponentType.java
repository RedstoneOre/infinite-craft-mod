package com.infinite_craft.element;

import java.util.NoSuchElementException;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public record ElementComponentType(String emoji, String name, String color, Identifier model) {
    public static final Codec<ElementComponentType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    Codec.STRING.fieldOf("emoji").forGetter(ElementComponentType::emoji),
    Codec.STRING.fieldOf("name").forGetter(ElementComponentType::name),
    Codec.STRING.fieldOf("color").forGetter(ElementComponentType::color),
    Identifier.CODEC.fieldOf("model").forGetter(ElementComponentType::model)
    ).apply(instance, ElementComponentType::new));

    public static ElementComponentType from(NbtCompound nbt) throws NoSuchElementException {
        String emoji = nbt.getString("emoji").orElseThrow();
        String name = ElementData.toTitleCase(nbt.getString("name").orElseThrow());
        String color = nbt.getString("color").orElseThrow();
        Identifier model = Optional.ofNullable(Identifier.tryParse(nbt.getString("model").orElseThrow())).orElseThrow();
        return new ElementComponentType(emoji, name, color, model);
    }
}
