package com.example.infinitygauntlet;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.Map;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, InfinityGauntletMod.MODID);

    /** Шесть камней: power_stone, space_stone, reality_stone, soul_stone, time_stone, mind_stone. */
    public static final Map<Stone, RegistryObject<Item>> STONES = new EnumMap<>(Stone.class);

    static {
        for (Stone s : Stone.values()) {
            if (s == Stone.SNAP) continue;
            STONES.put(s, ITEMS.register(s.key + "_stone",
                    () -> new StoneItem(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.EPIC), s)));
        }
    }

    public static final RegistryObject<Item> INFINITY_GAUNTLET = ITEMS.register("infinity_gauntlet",
            () -> new InfinityGauntletItem(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.EPIC)));
}
