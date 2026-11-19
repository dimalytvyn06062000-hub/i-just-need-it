package com.example.infinitygauntlet;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public enum Stone {
    POWER("power", ChatFormatting.DARK_PURPLE, 60),
    SPACE("space", ChatFormatting.BLUE, 40),
    REALITY("reality", ChatFormatting.RED, 60),
    SOUL("soul", ChatFormatting.GOLD, 400),
    TIME("time", ChatFormatting.GREEN, 400),
    MIND("mind", ChatFormatting.YELLOW, 300),
    SNAP("snap", ChatFormatting.WHITE, 20 * 120); // только у перчатки

    public final String key;
    public final ChatFormatting color;
    public final int cooldown; // тики

    Stone(String key, ChatFormatting color, int cooldown) {
        this.key = key;
        this.color = color;
        this.cooldown = cooldown;
    }

    public Stone next() {
        return values()[(ordinal() + 1) % values().length];
    }

    public Component displayName() {
        return Component.translatable("stone." + InfinityGauntletMod.MODID + "." + key).withStyle(color);
    }
}
