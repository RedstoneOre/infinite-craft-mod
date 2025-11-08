package com.infinite_craft.element.globaldata;

import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.PersistentStateType;

public class GlobalDiscoveringDataManager {
    private static final String KEY = "discovered_elements";

    public static GlobalDiscoveringData get(MinecraftServer server) {
        PersistentStateManager manager = server.getOverworld().getPersistentStateManager();
        return manager.getOrCreate(
            new PersistentStateType<>(
                KEY,
                GlobalDiscoveringData::create,
                GlobalDiscoveringData.CODEC,
                DataFixTypes.LEVEL_SUMMARY)
        );
    }

    public static void markDirty(MinecraftServer server) {
        get(server).markDirty();
    }
}
