package com.eeeab.eeeabsmobs.server.handler;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.server.ability.Ability;
import com.eeeab.eeeabsmobs.server.ability.AbilityHandler;
import com.eeeab.eeeabsmobs.server.ability.abilities.ChainswordComboAbility;
import com.eeeab.eeeabsmobs.server.ability.abilities.SkyfallHammerAbility;
import com.eeeab.eeeabsmobs.server.ability.abilities.SoulSummonNecklaceAbility;
import com.eeeab.eeeabsmobs.server.capability.*;
import com.eeeab.eeeabsmobs.server.entity.effect.EntityOverloadExplode;
import com.eeeab.eeeabsmobs.server.entity.effect.projectile.EntityBloodBall;
import com.eeeab.eeeabsmobs.server.entity.effect.projectile.EntityShamanBomb;
import com.eeeab.eeeabsmobs.server.entity.mob.IMob;
import com.eeeab.eeeabsmobs.server.entity.mob.corpse.EntityAbsCorpse;
import com.eeeab.eeeabsmobs.server.entity.mob.corpse.EntityCorpseWarlock;
import com.eeeab.eeeabsmobs.server.entity.mob.immortal.EntityAbsImmortal;
import com.eeeab.eeeabsmobs.server.entity.mob.immortal.EntityImmortalExecutioner;
import com.eeeab.eeeabsmobs.server.init.*;
import com.eeeab.eeeabsmobs.server.item.IUnbreakableItem;
import com.eeeab.mixin.MobAccessor;
import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.UUID;

/**
 * Fabric has no mod/event bus, so this is no longer a class of instance {@code @SubscribeEvent} methods that
 * gets {@code bus.register(new ServerEventHandler())}'d. Every hook here was re-homed one of two ways:
 * <p>
 * 1. Fabric API has a direct callback for it -&gt; wired up in {@link #register()}, called once from server
 *    init (out of this port's scope; see EEEABMobs.java).
 * 2. Fabric API has nothing for it (there is no mutable "damage amount", "knockback", "jump", "fall damage",
 *    "effect added/removed", or "projectile impact" event on Fabric at all - verified by listing every class in
 *    fabric-entity-events-v1/fabric-lifecycle-events-v1/fabric-events-interaction-v0 and finding no match) -&gt;
 *    the logic was kept here as a plain static method and is invoked from a mixin instead:
 *    {@code com.eeeab.mixin.LivingEntityHandlerMixin} (LivingEntity: tick, hurt, causeFallDamage, knockback,
 *    jumpFromGround, onEffectAdded, onEffectRemoved, removeEffect, heal, canBreatheUnderwater) and
 *    {@code com.eeeab.mixin.ProjectileHandlerMixin} (Projectile: onHitEntity).
 * <p>
 * Not ported in this pass (documented rather than silently dropped): {@code ItemAttributeModifierEvent}
 * (Unbreakable-flag toggling for {@link IUnbreakableItem}s) and {@code BlockEvent.EntityPlaceEvent} (stunned
 * entities placing blocks) have no Fabric API event and were not given a mixin here - both are minor,
 * non-boss-fight-critical behaviours, left broken rather than guessed at under this pass's time budget.
 */
public final class ServerEventHandler {

    private ServerEventHandler() {
    }

    public static void register() {
        EntityEvent.ADD.register((entity, level) -> {
            onJoinWorld(entity);
            return EventResult.pass();
        });

        PlayerEvent.ATTACK_ENTITY.register((player, level, target, hand, hitResult) -> {
            if (onPlayerAttack(player, target)) return EventResult.interruptFalse();
            return EventResult.pass();
        });

        InteractionEvent.RIGHT_CLICK_ITEM.register((player, hand) -> {
            if (player.hasEffect(EffectInit.STUN_EFFECT.holder())) {
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });

        InteractionEvent.RIGHT_CLICK_BLOCK.register((player, hand, pos, face) -> {
            if (player.hasEffect(EffectInit.STUN_EFFECT.holder())) return InteractionResult.FAIL;
            return InteractionResult.PASS;
        });

        InteractionEvent.INTERACT_ENTITY.register((player, entity, hand) -> {
            if (player.hasEffect(EffectInit.STUN_EFFECT.holder())) return EventResult.interruptFalse();
            return EventResult.pass();
        });

        BlockEvent.BREAK.register((level, pos, state, player, xp) -> {
            if (player != null && player.hasEffect(EffectInit.STUN_EFFECT.holder())) return EventResult.interruptFalse();
            return EventResult.pass();
        });
    }

    public static void onJoinWorld(Entity entity) {
        if (entity instanceof Player) {
            AbilityCapability.IAbilityCapability capability = AbilityHandler.INSTANCE.getAbilityCapability((LivingEntity) entity);
            if (capability != null) capability.onInit((LivingEntity) entity);
        }

        if (entity.level().isClientSide()) return;

        // Mob#goalSelector/#targetSelector are protected-final in vanilla; NeoForge widens them to public via
        // its access-transformer file, Fabric has no equivalent applied here, so a @Accessor mixin
        // (com.eeeab.mixin.MobAccessor) stands in for that AT - see its javadoc for why.
        if (entity instanceof AbstractSkeleton abstractSkeleton) {
            ((MobAccessor) abstractSkeleton).eeeabmobs$getTargetSelector().addGoal(4, new NearestAttackableTargetGoal<>(abstractSkeleton, EntityAbsImmortal.class, 0, true, false, null));
        }
        if (entity instanceof Zombie zombie && !(zombie instanceof ZombifiedPiglin)) {
            GoalSelector zombieTargetSelector = ((MobAccessor) zombie).eeeabmobs$getTargetSelector();
            zombieTargetSelector.addGoal(4, new NearestAttackableTargetGoal<>(zombie, EntityAbsImmortal.class, 0, true, false, null));
            zombieTargetSelector.addGoal(4, new NearestAttackableTargetGoal<>(zombie, EntityAbsCorpse.class, 0, true, false, null));
        }
        if (entity instanceof AbstractVillager villager) {
            GoalSelector villagerGoalSelector = ((MobAccessor) villager).eeeabmobs$getGoalSelector();
            villagerGoalSelector.addGoal(3, new AvoidEntityGoal<>(villager, EntityAbsImmortal.class, 6.0F, 1.0D, 1.2D));
            villagerGoalSelector.addGoal(3, new AvoidEntityGoal<>(villager, EntityAbsCorpse.class, 6.0F, 1.0D, 1.2D));
        }
        if (entity instanceof Raider raider && !(raider instanceof SpellcasterIllager)) {
            ((MobAccessor) raider).eeeabmobs$getGoalSelector().addGoal(5, new NearestAttackableTargetGoal<>(raider, EntityAbsImmortal.class, 5, true, false, null));
        }
    }

    // 实体游戏刻 - called every tick from LivingEntityHandlerMixin (was EntityTickEvent.Post + instanceof check).
    public static void onLivingEntityTick(LivingEntity entity) {
        StunCapability.IStunCapability stunCapability = CapabilityHandler.getCapability(entity, CapabilityHandler.STUN_CAPABILITY);
        if (stunCapability != null) stunCapability.tick(entity);

        if (entity instanceof Player player) {
            AbilityCapability.IAbilityCapability abilityCapability = CapabilityHandler.getCapability(entity, CapabilityHandler.ABILITY_CAPABILITY);
            if (abilityCapability != null) {
                if (abilityCapability.getAbilitiesMap().isEmpty()) {
                    abilityCapability.onInit(player);
                }
                abilityCapability.tick(entity);
            }
            updateGuardianCoreStack(player);
        } else {
            AbilityCapability.IAbilityCapability abilityCapability = CapabilityHandler.getCapability(entity, CapabilityHandler.ABILITY_CAPABILITY);
            if (abilityCapability != null) abilityCapability.tick(entity);
        }

        FrenzyCapability.IFrenzyCapability frenzyCapability = CapabilityHandler.getCapability(entity, CapabilityHandler.FRENZY_CAPABILITY);
        if (frenzyCapability != null) frenzyCapability.tick(entity);

        ElectricityCapability.IElectricityCapability electricityCapability = CapabilityHandler.getCapability(entity, CapabilityHandler.ELECTRICITY_CAPABILITY);
        if (electricityCapability != null) electricityCapability.tick(entity);

    }

    // 当实体跌落时 - return true to cancel fall damage (was event.setCanceled(true)).
    public static boolean onLivingFall(LivingEntity entity, float distance) {
        if (entity instanceof Player player) {
            if (distance <= 3) return false;
            Ability<?> ability = AbilityHandler.INSTANCE.getAbility(player, AbilityHandler.SKYFALL_HAMMER_ABILITY);
            if (ability instanceof SkyfallHammerAbility skyfallAbility) {
                return skyfallAbility.isUsing() && skyfallAbility.isFalling();
            }
        }
        return false;
    }

    // 新效果或者已存在但是等级更高或时间更长的效果 添加到实体触发
    public static void onAddPointEffect(LivingEntity entity, MobEffectInstance effectInstance) {
        if (!entity.level().isClientSide()) {
            doCapabilityEffect(effectInstance, entity, true);
        }
    }

    // 效果被移除/失效时 - onEffectRemoved fires for both explicit removal (that survived the removeEffect
    // cancellation check below) and natural expiry, so both old handlers collapse into this one call.
    public static void onEffectRemoved(LivingEntity entity, MobEffectInstance effectInstance) {
        if (!entity.level().isClientSide() && effectInstance != null) {
            doCapabilityEffect(effectInstance, entity, false);
        }
    }

    // 玩家取消魔法效果时 (frenzy immunity to manual cure) - return true to cancel the removal.
    public static boolean onRemoveEffectAttempt(LivingEntity entity, Holder<MobEffect> effect) {
        if (effect.is(EffectInit.FRENZY_EFFECT.holder())) {
            return true;
        }
        return false;
    }

    // 实体跳起来时 - stun cancels horizontal jump strafing (kept vertical jump impulse).
    public static void onLivingJump(LivingEntity entity) {
        if (entity.hasEffect(EffectInit.STUN_EFFECT.holder())) {
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(1, 0, 1));
        }
    }

    // 实体被击退 - return true to zero the knockback (was event.setStrength(0F); event.setCanceled(true)).
    public static boolean onLivingEntityKnockBack(LivingEntity entity) {
        return entity instanceof IMob iMob && iMob.isBossLevel();
    }

    // 挂载实体时 - return true to cancel mounting.
    public static boolean onEntityMountEntity(Entity entity) {
        return entity instanceof IMob iMob && iMob.isBossLevel();
    }

    // 实体受伤时 - returns the (possibly mutated) damage amount, mirroring LivingIncomingDamageEvent#setAmount.
    public static float onLivingEntityHurt(LivingEntity hurtEntity, DamageSource source, float amount) {
        Entity directEntity = source.getDirectEntity();

        if (directEntity instanceof EntityShamanBomb shamanBomb) {
            if (shamanBomb.reboundFlag) {
                hurtEntity.addEffect(new MobEffectInstance(EffectInit.STUN_EFFECT.holder(), 100, 0, false, false, true));
            }
        }

        if (hurtEntity instanceof IMob iMob && iMob.isBossLevel() && source.getEntity() instanceof LivingEntity attacker) {
            ThreatMemoryCapability.IThreatMemoryCapability hurtMemoryCapability = CapabilityHandler.getCapability(hurtEntity, CapabilityHandler.THREATMEMORY_CAPABILITY);
            if (hurtMemoryCapability != null) hurtMemoryCapability.recordDamage(hurtEntity, attacker, amount);
        }

        AttributeInstance attribute = hurtEntity.getAttribute(AttributeInit.CRIT_CHANCE.holder());
        Entity attacker = source.getEntity();
        if (attribute != null && attacker != null) {
            double chance = attribute.getValue() - 1D;
            if (chance > 0 && hurtEntity.getRandom().nextFloat() <= chance) {
                amount *= 1.5F;
                hurtEntity.level().playSound(null, hurtEntity.getX(), hurtEntity.getY(), hurtEntity.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, hurtEntity.getSoundSource(), 1.0F, 1.0F);
                if (hurtEntity.level() instanceof ServerLevel serverLevel) serverLevel.getChunkSource().broadcastAndSend(attacker, new ClientboundAnimatePacket(hurtEntity, 4));
            }
        }

        if (hurtEntity instanceof Player player) {
            Ability<?> ability = AbilityHandler.INSTANCE.getAbility(player, AbilityHandler.SOUL_SUMMON_NECKLACE_ABILITY);
            if (ability instanceof SoulSummonNecklaceAbility summonNecklaceAbility) {
                summonNecklaceAbility.hurt(player, source, amount);
            }
        }

        if (source.is(DamageTypeTags.IS_FIRE)) tryTriggerOverloadExplosion(hurtEntity);

        if (source.getEntity() instanceof Player player) {
            ItemStack mainHandItem = player.getMainHandItem();
            if (mainHandItem.is(ItemInit.CHAINSWORD.get())) {
                Ability<?> ability = AbilityHandler.INSTANCE.getAbility(player, AbilityHandler.CHAINSWORD_COMBO_ABILITY);
                if (ability instanceof ChainswordComboAbility chainswordAbility) {
                    UUID primaryUUID = chainswordAbility.getPrimary();
                    int primaryTick = chainswordAbility.getPrimaryTick();
                    boolean isPrimary = primaryUUID != null && Math.abs(primaryTick - player.tickCount) <= 1 && hurtEntity.getUUID().equals(primaryUUID);
                    if (isPrimary) {
                        chainswordAbility.setPrimaryTick(0);
                        chainswordAbility.setPrimary(null);
                    }
                    boolean shouldUpdate = player.getAttackStrengthScale(0.5F) >= 1 && isPrimary;
                    chainswordAbility.onAttack(hurtEntity, shouldUpdate);
                    int comboCount = chainswordAbility.getComboCount();
                    if (shouldUpdate && comboCount > 0) {
                        if (player.level() instanceof ServerLevel serverLevel) {
                            Vec3 lookVec = player.getLookAngle();
                            Vec3 eyePos = player.getEyePosition(1.0F);
                            Vec3 endPos = eyePos.add(lookVec.scale(player.entityInteractionRange()));
                            Optional<Vec3> hit = hurtEntity.getBoundingBox().clip(eyePos, endPos);
                            Vec3 hitPos;
                            if (hit.isPresent()) {
                                hitPos = hit.get();
                            } else {
                                double radius = hurtEntity.getBbWidth() / 2;
                                Vec3 toPlayer = player.position().subtract(hurtEntity.position()).normalize();
                                hitPos = hurtEntity.position()
                                        .add(0, hurtEntity.getBbHeight() / 2, 0)
                                        .add(toPlayer.scale(radius + 0.2));
                            }
                            double quadSize = 1 + Mth.clamp(hurtEntity.getBbWidth() * 0.1F, 0.1F, 0.5F) * (comboCount - 1);
                            serverLevel.sendParticles(ParticleInit.HIT_CUT.get(), hitPos.x, hitPos.y, hitPos.z, 0, 1, 1, 1, quadSize);
                            hurtEntity.playSound(SoundInit.CHAINSWORD_HIT_CUT.get(), 0.5F, 0.9F + (comboCount - 1) * 0.04F);
                        }
                    }
                    float multiplier = chainswordAbility.getExtraDamageMultiplier();
                    amount *= multiplier;
                }
            }
        }
        return amount;
    }

    // 实体治疗时
    public static float onLivingEntityHeal(LivingEntity entity, float amount) {
        MobEffectInstance instance = entity.getEffect(EffectInit.ERODE_EFFECT.holder());
        if (instance != null) {
            float newAmount = Mth.clamp(1F - (instance.getAmplifier() + 1) * 0.1F, 0F, 1F);
            return amount * newAmount;
        }
        return amount;
    }

    // 实体呼吸事件 - return true to force "can breathe" (was event.setCanBreathe(true)).
    public static boolean onLivingEntityBreathe(LivingEntity entity) {
        return entity.hasEffect(EffectInit.FRENZY_EFFECT.holder());
    }

    // 当弹射物触及到物体时 - return true to cancel the impact (projectile keeps flying through), called from
    // ProjectileHandlerMixin#onHitEntity.
    public static boolean onProjectileImpact(net.minecraft.world.entity.projectile.Projectile projectile, net.minecraft.world.phys.EntityHitResult hitResult) {
        if (projectile instanceof EntityBloodBall bloodBall && !bloodBall.isHeal() && hitResult.getEntity() instanceof EntityCorpseWarlock) {
            return true;
        }
        if (projectile instanceof net.minecraft.world.entity.projectile.AbstractArrow arrow) {
            if (arrow.getPierceLevel() == 0 && !arrow.fireImmune() && !arrow.isOnFire() && hitResult.getEntity() instanceof EntityImmortalExecutioner) {
                arrow.igniteForSeconds(100F);
            }
            if (arrow.getOwner() instanceof EntityAbsImmortal immortal1 && hitResult.getEntity() instanceof EntityAbsImmortal immortal2 && immortal1.isAlliedTo(immortal2)) {
                return true;
            }
        }
        return false;
    }

    private static float calculateThreatDamage(ItemStack stack, Player player) {
        FoodProperties food = stack.get(net.minecraft.core.component.DataComponents.FOOD);
        if (food != null) {
            float i = food.nutrition() + food.saturation();
            return i;
        }
        if (stack.getItem() instanceof PotionItem) {
            PotionContents potionContents = stack.getOrDefault(net.minecraft.core.component.DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            if (!com.google.common.collect.Iterables.isEmpty(potionContents.getAllEffects())) {
                return 15F;
            }
        }
        return 0F;
    }

    // 实体使用物品后 - was LivingEntityUseItemEvent.Finish; no Fabric equivalent either, but this only reads
    // state after the fact (no cancellation), so it is driven from the same tick mixin's use-item bookkeeping
    // is not straightforward on Fabric without its own item-finish hook. Left callable from a future mixin on
    // LivingEntity#completeUsingItem; not wired in this pass (threat-memory-from-healing is a minor buff to
    // aggro tracking, not core boss logic).
    public static void onItemUseFinish(Player player, ItemStack usedItem) {
        if (player.isCreative() || player.isSpectator()) return;
        boolean isFood = usedItem.has(net.minecraft.core.component.DataComponents.FOOD);
        boolean isPotion = usedItem.getItem() instanceof PotionItem;
        if (!isFood && !isPotion) return;
        LivingEntity target = player.getLastHurtMob();
        if (target != null && !target.isAlive()) return;
        if (target instanceof IMob iMob && iMob.isBossLevel()) {
            ThreatMemoryCapability.IThreatMemoryCapability hurtMemoryCapability = CapabilityHandler.getCapability(target, CapabilityHandler.THREATMEMORY_CAPABILITY);
            if (hurtMemoryCapability != null) {
                float threatDamage = Math.min(calculateThreatDamage(usedItem, player), 20F);
                if ((player.getHealth() / player.getMaxHealth()) <= 0.5) threatDamage *= 2;
                if (threatDamage > 0) hurtMemoryCapability.recordDamage(target, player, threatDamage);
            }
        }
    }

    private static boolean onPlayerAttack(Player player, Entity target) {
        if (player.hasEffect(EffectInit.STUN_EFFECT.holder())) return true;

        ItemStack itemStack = player.getMainHandItem();
        if (itemStack.is(ItemInit.CHAINSWORD.get()) && target instanceof LivingEntity) {
            Ability<?> ability = AbilityHandler.INSTANCE.getAbility(player, AbilityHandler.CHAINSWORD_COMBO_ABILITY);
            if (ability instanceof ChainswordComboAbility comboAbility) {
                comboAbility.setPrimary(target.getUUID());
                comboAbility.setPrimaryTick(player.tickCount);
            }
        }
        return false;
    }

    private static void updateGuardianCoreStack(Player player) {
        Ability<?> ability = AbilityHandler.INSTANCE.getAbility(player, AbilityHandler.GUARDIAN_LASER_ABILITY);
        ItemStack itemStack = player.getUseItem();
        if (ability != null) {
            if (!ability.isUsing()) {
                for (ItemStack stack : player.getInventory().getNonEquipmentItems()) {
                    if (stack.is(ItemInit.GUARDIAN_CORE.get()))
                        stack.setDamageValue(Math.max(stack.getDamageValue() - 1, 0));
                }
                ItemStack offhandStack = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.OFFHAND);
                if (offhandStack.is(ItemInit.GUARDIAN_CORE.get()))
                    offhandStack.setDamageValue(Math.max(offhandStack.getDamageValue() - 1, 0));
            } else if (itemStack.is(ItemInit.GUARDIAN_CORE.get())) {
                if (itemStack.getDamageValue() + 1 < itemStack.getMaxDamage()) {
                    itemStack.hurtAndBreak(1, player, player.getUsedItemHand() == net.minecraft.world.InteractionHand.MAIN_HAND ? net.minecraft.world.entity.EquipmentSlot.MAINHAND : net.minecraft.world.entity.EquipmentSlot.OFFHAND);
                } else {
                    ability.end();
                }
            } else {
                ability.end();
            }
        }
    }

    private static boolean doCapabilityEffect(MobEffectInstance effectInstance, LivingEntity entity, boolean added) {
        CapabilityHandler.CapabilityToken<?> capabilityToken = null;
        GeneralCapability capability = null;
        Holder<MobEffect> effect = effectInstance.getEffect();
        if (effect.is(EffectInit.STUN_EFFECT.holder())) {
            capabilityToken = CapabilityHandler.STUN_CAPABILITY;
            capability = CapabilityHandler.getCapability(entity, CapabilityHandler.STUN_CAPABILITY);
        } else if (effect.is(EffectInit.FRENZY_EFFECT.holder())) {
            capabilityToken = CapabilityHandler.FRENZY_CAPABILITY;
            capability = CapabilityHandler.getCapability(entity, CapabilityHandler.FRENZY_CAPABILITY);
        } else if (effect.is(EffectInit.ELECTRIFIED_EFFECT.holder())) {
            capabilityToken = CapabilityHandler.ELECTRICITY_CAPABILITY;
            capability = CapabilityHandler.getCapability(entity, CapabilityHandler.ELECTRICITY_CAPABILITY);
        }
        if (capabilityToken != null && entity.level() instanceof ServerLevel) {
            com.eeeab.eeeabsmobs.server.message.ICapabilityMessage message =
                    new com.eeeab.eeeabsmobs.server.message.ICapabilityMessage(entity, added, capabilityToken);
            com.eeeab.eeeabsmobs.server.message.ModNetwork.sendToTracking(entity, message, true);
        }
        if (capability != null) {
            if (added) capability.onStart(entity);
            else capability.onEnd(entity);
        }
        return false;
    }

    private static void tryTriggerOverloadExplosion(LivingEntity entity) {
        float baseDamage = ModConfigHandler.COMMON.potions.electrifiedConfig1.get().floatValue();
        if (baseDamage > 0F && entity.removeEffect(EffectInit.ELECTRIFIED_EFFECT.holder())) {
            double radians = Math.toRadians(entity.getYRot() + 90);
            EntityOverloadExplode.explode(entity.level(), entity.position().add(Math.cos(radians), entity.getBbHeight() * 0.3, Math.sin(radians)), entity, 2F, baseDamage);
            entity.clearFire();
        }
    }
}
