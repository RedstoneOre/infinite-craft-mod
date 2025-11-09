package com.infinite_craft.element;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.infinite_craft.InfiniteCraft;
import com.mojang.serialization.Codec;

import net.minecraft.component.ComponentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Identifier;

/**Save the discovered elements of a player*/
public class DiscoveringPlayerData {
	public static final Identifier DISCOVERED_ELEMENT_KEY=Identifier.of(InfiniteCraft.MOD_ID, "discovered_elements");

    public static final Codec<Map<String, ElementComponentType>> ELEMENT_MAP_CODEC =
            Codec.unboundedMap(Codec.STRING, ElementComponentType.CODEC);

    public static final Codec<DiscoveringPlayerData> CODEC =
        Codec.unboundedMap(Codec.STRING, ElementComponentType.CODEC)
            .xmap(DiscoveringPlayerData::new, DiscoveringPlayerData::getData)
            .orElse(new DiscoveringPlayerData(new HashMap<>()));


    public static final ComponentType<Map<String, ElementComponentType>> DISCOVERED_ELEMENTS =
            ComponentType.<Map<String, ElementComponentType>>builder()
                    .codec(ELEMENT_MAP_CODEC)
                    .build();

	public static void register() {
        Registry.register(Registries.DATA_COMPONENT_TYPE,
			DISCOVERED_ELEMENT_KEY,
			DISCOVERED_ELEMENTS);
    }

    private Map<String, ElementComponentType> data;
    
    public DiscoveringPlayerData(){
        data=new HashMap<>();
    }

    public DiscoveringPlayerData(Map<String, ElementComponentType> discoveredData){
        data=new HashMap<>(discoveredData);
    }
    /**
     * @param newElement - The new Element data
     * @return If the element is added
     */
    public boolean add(ElementComponentType newElement){
        if(data.containsKey(newElement.name())){
            return false;
        }
        data.put(newElement.name(), newElement);
        return true;
    }
    /**
     * @param elementName - The name of the element
     * @return If the element is removed
     */
    public boolean remove(String elementName){
        if(!data.containsKey(elementName)){
            return false;
        }
        data.remove(elementName);
        return true;
    }
    /**
     * @param elementName - The name of the element
     * @return The element, Optional
     */
    public Optional<ElementComponentType> get(String elementName){
        if(data.containsKey(elementName)){
            return Optional.of(data.get(elementName));
        }
        return Optional.empty();
    }

    public void writeTo(WriteView view){
        view.put(DISCOVERED_ELEMENT_KEY.toString(), ELEMENT_MAP_CODEC, data);
    }
    public void readFrom(ReadView view){
        data.clear();
        view.read(DISCOVERED_ELEMENT_KEY.toString(), ELEMENT_MAP_CODEC).ifPresent(
            newData -> {
                data=new HashMap<>(newData);
            }
        );
    }

    public NbtCompound toNbt(){
        NbtCompound result = new NbtCompound();
        data.forEach((key, value) -> {
            result.put(key, ElementComponentType.CODEC, value);
        });
        return result;
    }

    public Map<String, ElementComponentType> getData() {
        return data;
    }
}