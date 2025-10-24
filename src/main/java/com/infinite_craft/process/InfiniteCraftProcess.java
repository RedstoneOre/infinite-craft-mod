package com.infinite_craft.process;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.infinite_craft.InfiniteCraft;
import com.infinite_craft.InfiniteItem;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.SharedConstants;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.Registries;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InfiniteCraftProcess {

    /**
     * 异步执行请求并给予结果
     */
    public static void requestCraftResult(MinecraftServer server, ServerPlayerEntity player, BlockPos pos, CraftingScreenHandler handler) {

        // 🧱 1. 获取工作台格子物品
        StringBuilder itemList = new StringBuilder();
        int gridSize = 3;
        int minInputItemStack=0xff;
        final int exceptedTryCraftTicksF = 20 * 120;

        Slot[] inputs = new Slot[9];
        ItemStack[] mayReturn = new ItemStack[9];
        for (int i = 1; i <= 9; i++) {
            inputs[i - 1] = handler.getSlot(i);
        }
        for (int i = 0; i < 9; i++) {
            Slot slot=inputs[i];
            if (slot.getStack().isEmpty()) continue;
            ItemStack stack = slot.getStack();
            NbtCompound nbt = ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, stack)
                .resultOrPartial(error -> System.err.println("编码失败: " + error))
                .map(nbtElement -> (NbtCompound) nbtElement)
                .orElseThrow();
            nbt.remove("count");
            int row = i / 3;
            int col = i % 3;
            itemList.append(String.format("slot line %d col %d: %s\n", row, col, nbt.toString()));
            minInputItemStack=Math.min(slot.getStack().getCount(), minInputItemStack);
        }
        InfiniteCraft.LOGGER.info("Min Input Item Stack: {}", minInputItemStack);
        if(minInputItemStack==0xff) return;
        for (int i = 0; i < 9; i++) {
            Slot slot=inputs[i];
            if (slot.getStack().isEmpty()) continue;
            mayReturn[i]=slot.getStack().copy();
            mayReturn[i].setCount(minInputItemStack);
            slot.takeStack(minInputItemStack);
        }
        final int finalMinInputItemStack=minInputItemStack;
        new Thread(() -> {
            try {
                int exceptedTryCraftTicks = exceptedTryCraftTicksF;
                double progressStart=0;
                double progressCompleteRate=0.6;
                final double progressTarget=100;
                LoadingState loadingState = new LoadingState(player, progressStart, progressCompleteRate, progressTarget);
                loadingState.newLoadingProcessCustomEnd(1, 10);

                // 🧠 2. 构造 JSON 请求
                JsonObject body = JsonParser.parseString("""
                    {
                        "model": "deepseek-r1:8b",
                        "format": {
                            "type": "object",
                            "properties": {
                                "itemNbt": {"type": "string"},
                                "success": {"type": "boolean"}
                            },
                            "required": ["itemNbt", "success"]
                        },
                        "stream": false
                    }
                    """).getAsJsonObject();
                String gameVersion=SharedConstants.getGameVersion().name();
                body.addProperty("prompt", """
                    You are now generating a minecraft %s crafting result
                    The User is using %dx%d crafting grid, and the items are:
                    %s
                    output in `{"itemNbt": (string),"success": (boolean)}` json format
                    If the crafting should have a result, then set success to true, output the item in `{id: '...', count: ...i, components: {...}}` NBT format to `itemNbt` and make sure minecraft can parse `itemNbt`,
                    \tlike output `{id:"minecraft:copper_sword",count: 1i,components:{"minecraft:enchantments":{"minecraft:sharpness":2}}}` to `itemNbt` when the user craft a copper sword with a stick and 2 weathered copper blocks
                    \tMAKE SURE:
                    \t\tUse quotes and escapes(sometimes) when generating the NBT
                    \t\tAdd "minecraft:" namespace in component names, enchantment names, effect name etc.(Then don't forget to add quote)
                    \t\tFollow the minecraft item components document.
                    \t\tNever use any feature that been deprecated before %s or been added after %s
                    \tyou should firstly try to make it a vanilla item (maybe with enchantments or sth then write it in components) if you think the user really want it,
                    \tor use "%s" id then provide "minecraft:item_model":"..."("minecraft:barrier" etc.) and "minecraft:item_name":(mostly suggested format:){translate: "...", fallback: "..."} component and other additional components.
                    But if the crafting really really shouldn't and can't have a logical result, set success to false and set itemNbt to `{}`(PLEASE DO THIS LESS)
                    """.formatted(gameVersion, gridSize, gridSize, itemList, gameVersion, gameVersion, Registries.ITEM.getId(InfiniteItem.CUSTOM_CRAFTED_ITEM))
                );
                InfiniteCraft.LOGGER.info("Request body:\n{}", body.toString());

                // 🌐 3. HTTP POST 请求 + 重试机制
                ItemStack response = postWithRetry("http://localhost:11434/api/generate", body.toString(), 3, loadingState, exceptedTryCraftTicks);

                // 🎁 4. 解析返回结果
                if (response != null) {
                    InfiniteCraft.LOGGER.info("Request Result:\n{}", response.toString());
                    if (!response.isEmpty()) {
                        server.execute(() -> { // 回到主线程
                            for (int i = 0; i < finalMinInputItemStack; i++) {
                                if (player != null && player.isAlive()) {
                                    player.sendMessage(Text.literal("§a合成了: §r").append(
                                        DescribeItemStack(response)
                                    ), false);
                                    if (!player.getInventory().insertStack(response)) {
                                        player.dropItem(response, false);
                                    }
                                } else {
                                    World world = server.getWorld(World.OVERWORLD);
                                    if (world != null) {
                                        ItemEntity entity = new ItemEntity(world,
                                                pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                                                response);
                                        world.spawnEntity(entity);
                                    }
                                }
                            }
                        });
                    }
                } else {
                    InfiniteCraft.LOGGER.info("Request Failed\n");
                    server.execute(() -> {
                        for(ItemStack result : mayReturn){
                            if(result!=null && !result.isEmpty()){
                                if (player != null && player.isAlive()) {
                                    player.sendMessage(Text.literal("§a返还了物品: §r").append(
                                        DescribeItemStack(result)
                                    ), false);
                                    if (!player.getInventory().insertStack(result)) {
                                        player.dropItem(result, false);
                                    }
                                } else {
                                    World world = server.getWorld(World.OVERWORLD);
                                    if (world != null) {
                                        ItemEntity entity = new ItemEntity(world,
                                                pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                                                result);
                                        world.spawnEntity(entity);
                                    }
                                }
                            }
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "InfiniteCraft-HTTP").start();
    }

    /**
     * 重试 3 次请求
     */
    private static ItemStack postWithRetry(String urlStr, String body, int maxRetries, LoadingState loadingState, int exceptedTryCraftTicks) {
        for (int i = 0; i < maxRetries; i++) {
            loadingState.newLoadingProcess(exceptedTryCraftTicks);
            try {
                JsonObject json = doPost(urlStr, body);
                JsonObject response;
                try{
                    response = JsonParser.parseString(json.get("response").getAsString()).getAsJsonObject();
                } catch(Exception e){
                    throw new Exception("The AI api is kinda broken,Unable to Get Response!");
                }
                if (response.has("success") && response.has("itemNbt")) {
                    if(response.get("success").getAsBoolean()==false) return null;
                    try {
                        if(response.get("success").getAsBoolean()==true){
                            String nbtString = response.get("itemNbt").getAsString();
                            InfiniteCraft.LOGGER.info("Item NBT:\n"+nbtString);
                            return parseItemStackFromNbt(nbtString);
                        } else {
                            return null;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new Exception("The AI is too dumb!");
                    }
                }
                throw new Exception("The AI api is kinda broken,Illegal Response!");
            } catch (Exception e) {
                System.err.println("[InfiniteCraft] Require failed ( Retry " + (i + 1) + " ): " + e.getMessage());
                try {
                    Thread.sleep(1000L * (i + 1));
                } catch (InterruptedException ignored) {}
            }
        }
        loadingState.complete(5);
        return null;
    }

    /**
     * 执行 HTTP POST 请求
     */
    private static JsonObject doPost(String urlStr, String body) throws IOException {
        // if(true){JsonObject tmp = new JsonObject();tmp.addProperty("success", true);tmp.addProperty("itemNbt", "{\"id\":\"minecraft:diamond\",\"count\":1,\"components\":{\"enchantments\":{\"sharpness\":2}}}");JsonObject response = new JsonObject();response.addProperty("response", tmp.toString());return response;}
        URL url = URI.create(urlStr).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }

        try (InputStream is = conn.getInputStream();
             InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        }
    }

    /**
     * 从 NBT 字符串解析 ItemStack
     */
    private static ItemStack parseItemStackFromNbt(String nbtString) throws RuntimeException, CommandSyntaxException {
        InfiniteCraft.LOGGER.info("Parsing Item Stack From:\n" + nbtString);
        NbtCompound nbt = StringNbtReader.readCompound(nbtString);
        ItemStack itemStack = ItemStack.CODEC.parse(NbtOps.INSTANCE, nbt)
            .resultOrPartial(error -> {
                // 尝试 fallback：只提取 id 并构造最小合法物品
                if (nbt.contains("id") && nbt.get("id").getType()==NbtElement.STRING_TYPE) {
                    Identifier id = Identifier.of(nbt.getString("id").get());
                    Item item = Registries.ITEM.get(id);
                    if (item != Items.AIR) {
                        InfiniteCraft.LOGGER.warn("Fallback to minimal ItemStack for id '{}': {}", id, error);
                        return; // 继续 fallback
                    }
                }
                // 如果连 id 都没有，才抛出异常
                throw new RuntimeException("Failed to parse ItemStack: " + error);
            })
            .orElse(ItemStack.EMPTY);
        return itemStack;
    }

    private static MutableText DescribeItemStack(ItemStack stack){
        return Text.literal(String.valueOf(stack.getCount())+" * ")
            .setStyle(Style.EMPTY.withHoverEvent(
                new HoverEvent.ShowItem(stack)
            ))
            .append(
                stack.getName()
            );
    }
}