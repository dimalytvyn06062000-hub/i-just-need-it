package com.example.infinitygauntlet;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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

/** Отдельный Камень Бесконечности: ПКМ — способность, в инвентаре — пассивный эффект. */
public class StoneItem extends Item {
    private final Stone stone;

    public StoneItem(Properties properties, Stone stone) {
        super(properties);
        this.stone = stone;
    }

    public Stone getStone() {
        return stone;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }
        if (!level.isClientSide && level instanceof ServerLevel server) {
            StoneAbilities.activate(server, player, stone);
            player.getCooldowns().addCooldown(this, stone.cooldown);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!level.isClientSide && entity instanceof Player player && player.tickCount % 40 == 0) {
            StoneAbilities.applyPassive(player, stone);
        }
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(getDescriptionId(stack)).withStyle(stone.color);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        String base = "tooltip." + InfinityGauntletMod.MODID + "." + stone.key;
        tooltip.add(Component.translatable(base + ".active").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable(base + ".passive").withStyle(ChatFormatting.DARK_GRAY));
    }
}
