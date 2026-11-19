package com.example.infinitygauntlet;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(InfinityGauntletMod.MODID)
public class InfinityGauntletMod {
    public static final String MODID = "infinitygauntlet";

    public InfinityGauntletMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.ITEMS.register(bus);
        ModTabs.TABS.register(bus);
    }
}
