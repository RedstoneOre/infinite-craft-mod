package com.infinite_craft.process;

import java.util.NoSuchElementException;

import com.google.gson.JsonObject;
import com.infinite_craft.InfiniteCraft;
import com.infinite_craft.InfiniteItem;
import com.infinite_craft.element.ElementData;
import com.infinite_craft.element.ElementItems;
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
import net.minecraft.text.TextColor;
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
        ItemStack[] usedItem = new ItemStack[9];
        for (int i = 1; i <= 9; i++) {
            inputs[i - 1] = handler.getSlot(i);
        }
        for (int i = 0; i < 9; i++) {
            Slot slot=inputs[i];
            if (slot.getStack().isEmpty()) continue;
            ItemStack stack = slot.getStack();
            String itemDesc;
            try{
                if(ElementData.isElement(stack)){
                    itemDesc="Element " + ElementData.fromItem(stack).orElseThrow().toString();
                } else {
                    NbtCompound nbt = ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, stack)
                        .resultOrPartial(error -> {})
                        .map(nbtElement -> (NbtCompound) nbtElement)
                        .orElseThrow();
                    nbt.remove("count");
                    itemDesc="Item " + nbt.toString();
                }
            } catch(NoSuchElementException e) {
                itemDesc="What the heck is " + e.getMessage();
            }
            int row = i / 3;
            int col = i % 3;
            itemList.append(String.format("slot line %d col %d: %s\n", row, col, itemDesc));
            minInputItemStack=Math.min(slot.getStack().getCount(), minInputItemStack);
        }
        InfiniteCraft.LOGGER.info("Min Input Item Stack: {}", minInputItemStack);
        if(minInputItemStack==0xff) return;
        for (int i = 0; i < 9; i++) {
            Slot slot=inputs[i];
            if (slot.getStack().isEmpty()) continue;
            usedItem[i]=slot.getStack().copy();
            usedItem[i].setCount(minInputItemStack);
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

                // 🧠 2. 构造 prompt
                String additionalTip="";
                {
                    int itemStackCount=0, elementCount=0;
                    for(ItemStack stack : usedItem){
                        if(stack!=null && !stack.isEmpty()){
                            if(stack.getItem()==InfiniteItem.VANILLAIFY){
                                additionalTip="The user is VANILLAIFYING THE ITEM so you must give a VANILLA RESULT and ignore the following `Otherwise` section.";
                            }
                            if(ElementData.isElement(stack)){
                                ++elementCount;
                            }
                            ++itemStackCount;
                        }
                    }
                    if(additionalTip.isEmpty()){
                        if(itemStackCount==1){
                            additionalTip="Since the user only inputted 1 item, you should break it down or transmute it.";
                        } else if(itemStackCount==elementCount){
                            additionalTip="The user probably want you to create an element.";
                        }
                    }
                };
                String gameVersion=SharedConstants.getGameVersion().name();
                String prompt = """
                    You are now generating a minecraft %s crafting result
                    The User is using %dx%d crafting grid, and the items are:
                    %s
                    output in `{"itemNbt": (string),"success": (boolean)}` json format
                    %s
                    If the crafting should have a result, then set success to true, output the item in `{id: '...', count: ...i, components: {...}}` NBT format to `itemNbt` and make sure minecraft can parse `itemNbt`,
                    \tlike output `{id:"minecraft:copper_sword",count: 1i,components:{"minecraft:enchantments":{"minecraft:sharpness":2}}}` to `itemNbt` when the user craft a copper sword with a stick and 2 weathered copper blocks
                    MAKE SURE:
                    \tUse quotes and escapes(sometimes) when generating the NBT
                    \tAdd "minecraft:" namespace in component names, enchantment names, effect name etc.(Then don't forget to add quote)
                    \tFollow the minecraft item components document.
                    \tNever use any feature that been deprecated before %s or been added after %s
                    \tSince you are on Minecraft 1.21.10 with new text format, use nbt directly in the texts, like use {"minecraft:item_name": {text: "SAMPLE", italic: true}} instead of {"minecraft:item_name": "{\\"text\\": \\"SAMPLE\\", \\"italic\\": \\"true\\"}"}
                    \tBe especially careful when generating attribute modifiers(NO "generic.") to make sure it fit minecraft 1.21.10
                    If you think the result should be an ELEMENT, like the original Infinite Craft game, then describe the element in {"name":$name,"emoji":$emoji,"color":$color} format in `element`, and provide "minecraft:item_model" in `itemNbt`.
                    \tFor example, the user input element `Wind` and `Water`, you should return {"success": true, "element": {"name":"Wave","emoji":"🌊","color":"aqua"}, "itemNbt": "{components: {\\"minecraft:item_model\\": \\"minecraft:blue_stained_glass\\"}}"}
                    \tIf the element name have multiple words, simply separate them with spaces.
                    If you think the user want a vanilla item by using recipes that the minecraft community may say "mojang should add the recipe",
                    \tthen make it a vanilla item (maybe) with enchantments, attribute modifiers or sth then write it in components,
                    Otherwise I'm sure the user WANT YOU TO CREATE A NEW ONE,then:
                    \tIf it should be a modified vanilla item, like a long pickaxe, the use vanilla item id and give it special components
                    \tIf it is a brand new item, you should use "%s" id then provide "minecraft:item_model":"..."("minecraft:barrier" etc.) and "minecraft:item_name":(mostly suggested format:){translate: "...", fallback: "..."} component and maybe OTHER ADDITIONAL COMPONENTS to make it usable.
                    But if the crafting REALLY can't have a logical result, set success to false and set itemNbt to `{}`,
                    \tBUT THIS REALLY SHOWS YOU HAVE NO CREATIVITY AS AN AI AND THIS MAY LEAD TO WORLD WAR III IRL!SO DON'T DO THIS!
                    """.formatted(gameVersion, gridSize, gridSize, itemList, additionalTip, gameVersion, gameVersion, Registries.ITEM.getId(InfiniteItem.CUSTOM_CRAFTED_ITEM));

                // 🌐 3. HTTP POST 请求 + 重试机制
                ItemStack response = postWithRetry(prompt, player, 3, loadingState, exceptedTryCraftTicks);

                // 🎁 4. 解析返回结果
                if (response != null) {
                    InfiniteCraft.LOGGER.info("Request Result:\n{}", response.toString());
                    if (!response.isEmpty()) {
                        server.execute(() -> { // 回到主线程
                            for (int i = 0; i < finalMinInputItemStack; i++) {
                                if (player != null && player.isAlive()) {
                                    player.sendMessage(Text.translatableWithFallback("chat.message.infinite_craft.craft.success","Crafted: ")
                                        .setStyle(
                                            Style.EMPTY.withColor(TextColor.parse("lime").result().orElse(TextColor.fromRgb(0xbfff00)))
                                        )
                                        .append(
                                            DescribeItemStack(response)
                                            .setStyle(
                                                Style.EMPTY.withColor(TextColor.parse("white").result().orElse(TextColor.fromRgb(0xffffff)))
                                            )
                                        ),
                                    false);
                                    ItemStack copiedItemStack = response.copy();
                                    if (!player.getInventory().insertStack(copiedItemStack)) {
                                        player.dropItem(copiedItemStack, false);
                                    }
                                } else {
                                    World world = server.getWorld(World.OVERWORLD);
                                    if (world != null) {
                                        ItemStack copiedItemStack = response.copy();
                                        ItemEntity entity = new ItemEntity(world,
                                                pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                                                copiedItemStack);
                                        world.spawnEntity(entity);
                                    }
                                }
                            }
                        });
                    }
                } else {
                    InfiniteCraft.LOGGER.info("Request Failed\n");
                    server.execute(() -> {
                        for(ItemStack result : usedItem){
                            if(result!=null && !result.isEmpty()){
                                if (player != null && player.isAlive()) {
                                    player.sendMessage(Text.translatableWithFallback("chat.message.infinite_craft.craft.return","Returned: ")
                                        .setStyle(
                                            Style.EMPTY.withColor(TextColor.parse("lime").result().orElse(TextColor.fromRgb(0xbfff00)))
                                        )
                                        .append(
                                            DescribeItemStack(result)
                                            .setStyle(
                                                Style.EMPTY.withColor(TextColor.parse("white").result().orElse(TextColor.fromRgb(0xffffff)))
                                            )
                                        ),
                                    false);
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
                InfiniteCraft.LOGGER.error(e.getMessage());
            }
        }, "InfiniteCraft-HTTP").start();
    }

    /**
     * Ask Ai and return a item stack
     */
    private static ItemStack postWithRetry(String prompt, ServerPlayerEntity player, int maxRetries, LoadingState loadingState, int exceptedTryCraftTicks) {
        for (int i = 0; i < maxRetries; i++) {
            loadingState.newLoadingProcess(exceptedTryCraftTicks);
            try {
                JsonObject response = AiApi.doPost(prompt, player);
                InfiniteCraft.LOGGER.info("API Response:\n{}", response.toString());
                if (response.has("success") && response.has("itemNbt")) {
                    if(response.get("success").getAsBoolean()==false) return null;
                    try {
                        String nbtString = response.get("itemNbt").getAsString();
                        InfiniteCraft.LOGGER.info("Item NBT:\n"+nbtString);
                        if(!response.has("element")){
                            return parseItemStackFromNbt(nbtString);
                        } else {
                            NbtCompound itemNbt = StringNbtReader.readCompound(nbtString).asCompound().get();
                            JsonObject elementData = response.get("element").getAsJsonObject();
                            return ElementItems.generateElement(new ElementData(
                                elementData.get("emoji").getAsString(),
                                elementData.get("name").getAsString(),
                                elementData.get("color").getAsString()
                            ), itemNbt);
                        }
                    } catch (Exception e) {
                        InfiniteCraft.LOGGER.error(e.getMessage());
                        throw new Exception("The AI is too dumb!"+e.getMessage());
                    }
                }
                throw new Exception("The AI api is kinda broken,Illegal Response!");
            } catch (Exception e) {
                System.err.println("[InfiniteCraft] Request failed ( Retry " + (i + 1) + " ): " + e.getMessage());
                try {
                    Thread.sleep(1000L * (i + 1));
                } catch (InterruptedException ignored) {}
            }
        }
        loadingState.complete(5);
        return null;
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