package com.example.infinitygauntlet;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/** Все способности камней. Используются и отдельными камнями, и перчаткой. */
public final class StoneAbilities {
    private StoneAbilities() {}

    /** Активная способность (ПКМ). Вызывать только на сервере. */
    public static void activate(ServerLevel level, Player player, Stone stone) {
        switch (stone) {
            case POWER -> {
                Vec3 t = player.pick(48.0D, 1.0F, false).getLocation();
                level.explode(player, t.x, t.y, t.z, 4.0F, Level.ExplosionInteraction.MOB);
            }
            case SPACE -> {
                HitResult hit = player.pick(64.0D, 1.0F, false);
                Vec3 t = hit.getLocation();
                if (hit instanceof BlockHitResult bhr && hit.getType() == HitResult.Type.BLOCK) {
                    BlockPos p = bhr.getBlockPos().relative(bhr.getDirection());
                    t = new Vec3(p.getX() + 0.5D, p.getY(), p.getZ() + 0.5D);
                }
                if (player.randomTeleport(t.x, t.y, t.z, true)) {
                    player.fallDistance = 0;
                    play(level, player, SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
                }
            }
            case REALITY -> {
                Vec3 t = player.pick(48.0D, 1.0F, false).getLocation();
                for (int i = 0; i < 3; i++) {
                    LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
                    if (bolt != null) {
                        bolt.moveTo(t.x + level.random.nextInt(5) - 2, t.y, t.z + level.random.nextInt(5) - 2);
                        level.addFreshEntity(bolt);
                    }
                }
            }
            case SOUL -> {
                player.setHealth(player.getMaxHealth());
                player.getFoodData().setFoodLevel(20);
                player.getFoodData().setSaturation(20.0F);
                player.removeAllEffects();
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 2));
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 1200, 4));
                level.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, player.getX(), player.getY() + 1, player.getZ(),
                        40, 0.5, 0.8, 0.5, 0.3);
                play(level, player, SoundEvents.TOTEM_USE, 1.0F, 1.0F);
            }
            case TIME -> {
                AABB box = player.getBoundingBox().inflate(16.0D);
                for (Mob m : level.getEntitiesOfClass(Mob.class, box)) {
                    m.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 10));
                    m.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 4));
                }
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 3));
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 200, 3));
                play(level, player, SoundEvents.BEACON_ACTIVATE, 1.0F, 0.5F);
            }
            case MIND -> {
                AABB box = player.getBoundingBox().inflate(20.0D);
                List<Mob> mobs = level.getEntitiesOfClass(Mob.class, box);
                for (Mob m : mobs) {
                    m.addEffect(new MobEffectInstance(MobEffects.GLOWING, 300, 0));
                    if (mobs.size() > 1) {
                        Mob other;
                        do {
                            other = mobs.get(level.random.nextInt(mobs.size()));
                        } while (other == m);
                        m.setTarget(other);
                    }
                }
                play(level, player, SoundEvents.EVOKER_CAST_SPELL, 1.0F, 1.0F);
            }
            case SNAP -> {
                AABB box = player.getBoundingBox().inflate(30.0D);
                for (Mob m : level.getEntitiesOfClass(Mob.class, box)) {
                    if (m instanceof TamableAnimal t && t.isTame()) continue; // питомцев не трогаем
                    if (level.random.nextBoolean()) {
                        level.sendParticles(ParticleTypes.ASH, m.getX(), m.getY() + m.getBbHeight() / 2, m.getZ(),
                                40, 0.4, 0.5, 0.4, 0.02);
                        m.discard();
                    }
                }
                play(level, player, SoundEvents.WITHER_DEATH, 1.0F, 0.6F);
            }
        }
    }

    /** Пассивный эффект, пока камень лежит в инвентаре. Вызывать только на сервере. */
    public static void applyPassive(Player player, Stone stone) {
        MobEffect effect = switch (stone) {
            case POWER -> MobEffects.DAMAGE_BOOST;
            case SPACE -> MobEffects.MOVEMENT_SPEED;
            case REALITY -> MobEffects.FIRE_RESISTANCE;
            case SOUL -> MobEffects.REGENERATION;
            case TIME -> MobEffects.DIG_SPEED;
            case MIND -> MobEffects.NIGHT_VISION;
            default -> null;
        };
        if (effect != null) {
            player.addEffect(new MobEffectInstance(effect, 260, 0, true, false, true));
        }
    }

    public static void play(Level level, Player player, SoundEvent sound, float vol, float pitch) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(), sound, SoundSource.PLAYERS, vol, pitch);
    }
}
