package com.example.infinitygauntlet;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, InfinityGauntletMod.MODID);

    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register("main", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + InfinityGauntletMod.MODID + ".main"))
                    .icon(() -> new ItemStack(ModItems.INFINITY_GAUNTLET.get()))
                    .displayItems((params, output) -> {
                        for (Stone s : Stone.values()) {
                            if (s != Stone.SNAP) output.accept(ModItems.STONES.get(s).get());
                        }
                        output.accept(ModItems.INFINITY_GAUNTLET.get());
                    })
                    .build());
}
