package com.example.infinitygauntlet;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class InfinityGauntletItem extends Item {
    private static final String TAG = "Stone";

    public InfinityGauntletItem(Properties properties) {
        super(properties);
    }

    public static Stone getStone(ItemStack stack) {
        int id = stack.hasTag() ? stack.getTag().getInt(TAG) : 0;
        Stone[] all = Stone.values();
        return all[Math.floorMod(id, all.length)];
    }

    private static void setStone(ItemStack stack, Stone stone) {
        stack.getOrCreateTag().putInt(TAG, stone.ordinal());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Shift + ПКМ — переключить камень
        if (player.isShiftKeyDown()) {
            Stone next = getStone(stack).next();
            setStone(stack, next);
            if (!level.isClientSide) {
                player.displayClientMessage(
                        Component.translatable("msg." + InfinityGauntletMod.MODID + ".selected", next.displayName()), true);
                StoneAbilities.play(level, player, SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F, 0.7F + 0.1F * next.ordinal());
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }
        if (!level.isClientSide && level instanceof ServerLevel server) {
            Stone stone = getStone(stack);
            StoneAbilities.activate(server, player, stone);
            player.getCooldowns().addCooldown(this, stone.cooldown);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    /** Пока перчатка в инвентаре — действуют пассивные эффекты всех шести камней. */
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!level.isClientSide && entity instanceof Player player && player.tickCount % 40 == 0) {
            for (Stone s : Stone.values()) {
                StoneAbilities.applyPassive(player, s);
            }
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("msg." + InfinityGauntletMod.MODID + ".selected", getStone(stack).displayName())
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip." + InfinityGauntletMod.MODID + ".use").withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable("tooltip." + InfinityGauntletMod.MODID + ".switch").withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable("tooltip." + InfinityGauntletMod.MODID + ".passive_all").withStyle(ChatFormatting.DARK_GRAY));
    }
}
