package com.infinite_craft.process;

import net.minecraft.server.network.ServerPlayerEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import com.infinite_craft.networking.InfiniteCraftNetworking;

import java.util.WeakHashMap;

public class InfiniteCraftFakeProgressTask {

    private static final WeakHashMap<ServerPlayerEntity, TaskState> activeTasks = new WeakHashMap<>();

    public static void start(ServerPlayerEntity player, double start, double end, int expectedTicks) {
        TaskState state = new TaskState(start, end, expectedTicks);
        activeTasks.put(player, state);
    }

    public static void registerTickHandler() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            activeTasks.forEach((player, state) -> {
                if (state.ticksElapsed >= state.expectedTicks) return;

                state.ticksElapsed++;
                int progress = (int) state.interpolate();
                if( progress != state.lastProgress ){
                    InfiniteCraftNetworking.sendArrowProgress(player, progress);
                    state.lastProgress=progress;
                }
            });

            // 清理已完成的任务
            activeTasks.entrySet().removeIf(entry -> entry.getValue().ticksElapsed >= entry.getValue().expectedTicks);
        });
    }

    private static double interpolate(double start, double end, double t) {
        return start + (end - start) * t;
    }

    public static void interrupt(ServerPlayerEntity player) {
        TaskState state = activeTasks.get(player);
        if (state != null) {
            // 立即发送最终进度
            InfiniteCraftNetworking.sendArrowProgress(player, (int) state.end);

            // 移除任务，停止 tick 更新
            activeTasks.remove(player);
        }
    }

    public static double getProgress(ServerPlayerEntity player) {
        TaskState state = activeTasks.get(player);
        return state.interpolate();
    }

    private static class TaskState {
        final double start;
        final double end;
        final int expectedTicks;
        int ticksElapsed = 0;
        int lastProgress = -1;

        TaskState(double start, double end, int expectedTicks) {
            this.start = start;
            this.end = end;
            this.expectedTicks = expectedTicks;
        }

        public double interpolate(){
            return InfiniteCraftFakeProgressTask.interpolate(start, end, (double) ticksElapsed / expectedTicks);
        }
    }
}
