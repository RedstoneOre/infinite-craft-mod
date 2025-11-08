package com.infinite_craft.element.globaldata;

import com.infinite_craft.element.DiscoveringPlayerData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.PersistentState;

public class GlobalDiscoveringData extends PersistentState {
    private final DiscoveringPlayerData discovered;

    public GlobalDiscoveringData(DiscoveringPlayerData discovered) {
        this.discovered = discovered;
    }

    public static GlobalDiscoveringData create(){
        return new GlobalDiscoveringData(new DiscoveringPlayerData());
    }

    public DiscoveringPlayerData getDiscovered() {
        return discovered;
    }

    // ✅ CODEC: 把 discovered 放在 "discovered" 键下
    public static final Codec<GlobalDiscoveringData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DiscoveringPlayerData.CODEC.fieldOf("discovered").forGetter(GlobalDiscoveringData::getDiscovered)
    ).apply(instance, GlobalDiscoveringData::new));
}
