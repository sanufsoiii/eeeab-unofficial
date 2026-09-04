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
import com.eeeab.eeeabsmobs.server.message.ICapabilityMessage;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

//服务端事件处理器
public final class ServerEventHandler {

    /**
     * NeoForge's ItemAttributeModifierEvent (fired on demand whenever a stack's attribute modifiers/tooltip
     * are computed) does not exist in Forge - verified against the real jar, the whole
     * net.minecraftforge.event package there has no such class, and this Forge version never had one either
     * (it is a NeoForge-only addition). There is no equivalent hook to subscribe to, so instead of computing
     * this on demand, it is driven from the player tick (already subscribed below) over the player's held and
     * worn items. This is a behavioural substitution, not a rename: the Unbreakable component now updates at
     * most once per tick rather than exactly when the game asks for attribute modifiers/tooltip, which is
     * unobservable for this mod's use (config-driven per-item unbreakability), but is a real difference and
     * should be re-checked if that assumption stops holding.
     */
    private static void updateUnbreakableFlag(ItemStack itemStack) {
        // 1.20.1 stored "Unbreakable" as arbitrary item NBT; 1.21 moved it to the DataComponents.UNBREAKABLE
        // data component (net.minecraft.world.item.component.Unbreakable). Arbitrary NBT still exists via the
        // CUSTOM_DATA component (net.minecraft.world.item.component.CustomData), used here only to remember
        // whether *this* code set the flag (so a player/anvil-applied Unbreakable is never touched).
        if (itemStack.getItem() instanceof IUnbreakableItem unbreakableItem) {
            boolean hasUnbreakable = itemStack.has(DataComponents.UNBREAKABLE);
            boolean codeAddedFlag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean("CodeAddFlag");
            if (!unbreakableItem.canBreakItem()) {
                if (!hasUnbreakable) {
                    itemStack.set(DataComponents.UNBREAKABLE, new Unbreakable(false));
                    CustomData.update(DataComponents.CUSTOM_DATA, itemStack, tag -> tag.putBoolean("CodeAddFlag", true));
                }
            } else if (hasUnbreakable && codeAddedFlag) {
                itemStack.remove(DataComponents.UNBREAKABLE);
                CustomData.update(DataComponents.CUSTOM_DATA, itemStack, tag -> tag.remove("CodeAddFlag"));
            }
        }
    }

    //实体加载到世界时
    @SubscribeEvent
    public void onJoinWorld(final EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            AbilityCapability.IAbilityCapability capability = AbilityHandler.INSTANCE.getAbilityCapability((LivingEntity) entity);
            if (capability != null) capability.onInit((LivingEntity) entity);
        }

        if (event.getLevel().isClientSide) return;

        if (entity instanceof AbstractSkeleton abstractSkeleton) {
            abstractSkeleton.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(abstractSkeleton, EntityAbsImmortal.class, 0, true, false, null));
        }
        if (entity instanceof Zombie zombie && !(zombie instanceof ZombifiedPiglin)) {
            zombie.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(zombie, EntityAbsImmortal.class, 0, true, false, null));
            zombie.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(zombie, EntityAbsCorpse.class, 0, true, false, null));
        }
        if (entity instanceof AbstractVillager villager) {
            villager.goalSelector.addGoal(3, new AvoidEntityGoal<>(villager, EntityAbsImmortal.class, 6.0F, 1.0D, 1.2D));
            villager.goalSelector.addGoal(3, new AvoidEntityGoal<>(villager, EntityAbsCorpse.class, 6.0F, 1.0D, 1.2D));
        }
        if (entity instanceof Raider raider && !(raider instanceof SpellcasterIllager)) {
            raider.goalSelector.addGoal(5, new NearestAttackableTargetGoal<>(raider, EntityAbsImmortal.class, 5, true, false, null));
        }
    }

    //实体游戏刻
    // Forge 1.21.1 still has LivingEvent.LivingTickEvent (verified against the real jar with javap) - the
    // NeoForge generic EntityTickEvent.Post redesign does not apply here, so this goes back to the original
    // Forge 1.20.1 event type.
    @SubscribeEvent
    public void onLivingEntityTick(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() instanceof LivingEntity entity) {
            StunCapability.IStunCapability stunCapability = CapabilityHandler.getCapability(entity, CapabilityHandler.STUN_CAPABILITY);
            if (stunCapability != null) {
                stunCapability.tick(entity);
            }

            AbilityCapability.IAbilityCapability abilityCapability = CapabilityHandler.getCapability(entity, CapabilityHandler.ABILITY_CAPABILITY);
            if (abilityCapability != null) {
                abilityCapability.tick(entity);
            }

            FrenzyCapability.IFrenzyCapability frenzyCapability = CapabilityHandler.getCapability(entity, CapabilityHandler.FRENZY_CAPABILITY);
            if (frenzyCapability != null) {
                frenzyCapability.tick(entity);
            }

            ElectricityCapability.IElectricityCapability electricityCapability = CapabilityHandler.getCapability(entity, CapabilityHandler.ELECTRICITY_CAPABILITY);
            if (electricityCapability != null) {
                electricityCapability.tick(entity);
            }

            //if (!entity.level().isClientSide && entity.getRemainingFireTicks() > 0) tryTriggerOverloadExplosion(entity, null);
        }
    }

    //玩家游戏刻
    // Forge 1.21.1 still has the original TickEvent.PlayerTickEvent with a Phase.START/END field (verified
    // against the real jar) - the NeoForge Pre/Post split does not apply here.
    @SubscribeEvent
    public void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            updateGuardianCoreStack(event);
            for (ItemStack stack : event.player.getInventory().items) updateUnbreakableFlag(stack);
            for (ItemStack stack : event.player.getInventory().armor) updateUnbreakableFlag(stack);
            for (ItemStack stack : event.player.getInventory().offhand) updateUnbreakableFlag(stack);
        }
    }

    //当实体跌落时
    @SubscribeEvent
    public void onLivingFall(LivingFallEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (event.getDistance() <= 3) return;
            Ability<?> ability = AbilityHandler.INSTANCE.getAbility(player, AbilityHandler.SKYFALL_HAMMER_ABILITY);
            if (ability != null) {
                if (ability instanceof SkyfallHammerAbility skyfallAbility) {
                    if (skyfallAbility.isUsing() && skyfallAbility.isFalling()) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    ////当实体生命值≤0时触发
    //@SubscribeEvent
    //public void onLivingDeathEvent(LivingDeathEvent event) {
    //    LivingEntity entity = event.getEntity();
    //    if (event.isCancelable() && entity instanceof EEEABMobEntity && entity.getHealth() > 0) {
    //        event.setCanceled(true);
    //    }
    //}

    //新效果或者已存在但是等级更高或时间更长的效果 添加到实体触发
    @SubscribeEvent
    public void onAddPointEffect(MobEffectEvent.Added event) {
        MobEffectInstance effectInstance = event.getEffectInstance();
        if (!event.getEntity().level().isClientSide()) {
            doCapabilityEffect(effectInstance, event.getEntity(), true, false);
        }
    }

    //效果被移除时
    @SubscribeEvent
    public void onRemovePotionEffect(MobEffectEvent.Remove event) {
        MobEffectInstance effectInstance = event.getEffectInstance();
        if (!event.getEntity().level().isClientSide() && effectInstance != null) {
            if (doCapabilityEffect(effectInstance, event.getEntity(), false, true)) {
                event.setCanceled(true);
            }
        }
    }

    //药水失效时
    @SubscribeEvent
    public void onExpirePotionEffect(MobEffectEvent.Expired event) {
        MobEffectInstance effectInstance = event.getEffectInstance();
        if (!event.getEntity().level().isClientSide() && effectInstance != null) {
            doCapabilityEffect(effectInstance, event.getEntity(), false, false);
        }
    }

    //实体跳起来时
    @SubscribeEvent
    public void onLivingJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntity() != null) {
            LivingEntity entity = event.getEntity();
            if (entity.hasEffect(EffectInit.STUN_EFFECT.getHolder().orElseThrow())) {
                entity.setDeltaMovement(entity.getDeltaMovement().multiply(1, 0, 1));
            }
        }
    }

    //玩家攻击实体时
    @SubscribeEvent
    public void onPlayerAttack(AttackEntityEvent event) {
        if (event.getEntity().hasEffect(EffectInit.STUN_EFFECT.getHolder().orElseThrow())) {
            event.setCanceled(true);
            return;
        }

        ItemStack itemStack = event.getEntity().getMainHandItem();
        if (itemStack.is(ItemInit.CHAINSWORD.get())) {
            Ability<?> ability = AbilityHandler.INSTANCE.getAbility(event.getEntity(), AbilityHandler.CHAINSWORD_COMBO_ABILITY);
            if (ability instanceof ChainswordComboAbility comboAbility) {
                comboAbility.setPrimary(event.getTarget().getUUID());
                comboAbility.setPrimaryTick(event.getEntity().tickCount);
            }
        }
    }

    //玩家右键物品时
    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.RightClickItem event) {
        if (event.getEntity().hasEffect(EffectInit.STUN_EFFECT.getHolder().orElseThrow())) {
            event.setCanceled(true);
        }
    }

    //玩家右键方块时
    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
        if (event.getEntity().hasEffect(EffectInit.STUN_EFFECT.getHolder().orElseThrow())) {
            event.setCanceled(true);
        }
    }

    //玩家右键实体时
    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getEntity().hasEffect(EffectInit.STUN_EFFECT.getHolder().orElseThrow())) {
            event.setCanceled(true);
        }
    }

    // PlayerInteractEvent.RightClickEmpty does not implement ICancellableEvent (never did in Forge either -
    // event.isCancelable() was always false here, so setCanceled() was already dead code). Nothing left to do.

    //玩家将要打破方块时
    @SubscribeEvent
    public void onBreakBlock(BlockEvent.BreakEvent event) {
        if (event.getPlayer().hasEffect(EffectInit.STUN_EFFECT.getHolder().orElseThrow())) {
            event.setCanceled(true);
        }
    }

    //玩家放置方块前时
    @SubscribeEvent
    public void onPlaceBlock(BlockEvent.EntityPlaceEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity living) {
            if (living.hasEffect(EffectInit.STUN_EFFECT.getHolder().orElseThrow())) {
                event.setCanceled(true);
            }
        }
    }

    //实体使用物品时
    // Only LivingEntityUseItemEvent.Start/.Tick/.Stop implement ICancellableEvent now (the base class and
    // .Finish do not - matching that you can't cancel something that already finished); switched to .Start
    // since the intent here is to prevent starting the item use in the first place.
    @SubscribeEvent
    public void onUseItem(LivingEntityUseItemEvent.Start event) {
        LivingEntity living = event.getEntity();
        if (living.hasEffect(EffectInit.STUN_EFFECT.getHolder().orElseThrow())) {
            event.setCanceled(true);
        }
    }

    //实体使用物品后
    @SubscribeEvent
    public void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
        LivingEntity user = event.getEntity();
        if (!(user instanceof Player player)) return;
        if (player.isCreative() || player.isSpectator()) return;
        ItemStack usedItem = event.getItem();
        // ItemStack#isEdible() was removed - food-ness moved entirely onto DataComponents.FOOD.
        boolean isFood = usedItem.has(DataComponents.FOOD);
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

    //实体被击退
    @SubscribeEvent
    public void onLivingEntityKnockBack(LivingKnockBackEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof IMob iMob && iMob.isBossLevel()) {
            event.setStrength(0F);
            event.setCanceled(true);
        }
    }

    //挂载实体时
    @SubscribeEvent
    public void onEntityMountEntity(EntityMountEvent event) {
        Entity entity = event.getEntityMounting();
        if (entity instanceof IMob iMob && iMob.isBossLevel()) {
            event.setCanceled(true);
        }
    }

    //实体受伤时
    // LivingIncomingDamageEvent is a NeoForge rename/reshape of Forge's own LivingHurtEvent (fired
    // pre-mitigation, amount mutable) - Forge 1.21.1 still has LivingHurtEvent with the same
    // getSource()/getEntity()/getAmount()/setAmount() shape (verified against the real jar with javap), so
    // this goes back to the original Forge type.
    @SubscribeEvent
    public void onLivingEntityHurt(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        Entity directEntity = source.getDirectEntity();
        LivingEntity hurtEntity = event.getEntity();

        if (directEntity instanceof EntityShamanBomb shamanBomb) {
            if (shamanBomb.reboundFlag) {
                hurtEntity.addEffect(new MobEffectInstance(EffectInit.STUN_EFFECT.getHolder().orElseThrow(), 100, 0, false, false, true));
            }
        }

        //FrenzyCapability.IFrenzyCapability frenzyCapability = CapabilityHandler.getCapability(hurtEntity, CapabilityHandler.FRENZY_CAPABILITY);
        //if (frenzyCapability != null && frenzyCapability.flag() && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && !source.is(DamageTypeTags.BYPASSES_ARMOR)) {
        //    float damage = event.getAmount();
        //    if (hurtEntity.getHealth() > 1F) {
        //        //至多减少50%伤害
        //        damage -= damage * (Math.min((frenzyCapability.getLevel() + 1F), 5)) * 0.1F;
        //    }
        //    event.setAmount(damage);
        //}
        if (hurtEntity instanceof IMob iMob && iMob.isBossLevel() && source.getEntity() instanceof LivingEntity attacker) {
            ThreatMemoryCapability.IThreatMemoryCapability hurtMemoryCapability = CapabilityHandler.getCapability(hurtEntity, CapabilityHandler.THREATMEMORY_CAPABILITY);
            if (hurtMemoryCapability != null) hurtMemoryCapability.recordDamage(hurtEntity, attacker, event.getAmount());
        }

        AttributeInstance attribute = hurtEntity.getAttribute(AttributeInit.CRIT_CHANCE.getHolder().orElseThrow());
        Entity attacker = source.getEntity();
        if (attribute != null && attacker != null) {
            double chance = attribute.getValue() - 1D;
            if (chance > 0 && hurtEntity.getRandom().nextFloat() <= chance) {
                float damage = event.getAmount();
                damage *= 1.5F;
                event.setAmount(damage);
                hurtEntity.level().playSound(null, hurtEntity.getX(), hurtEntity.getY(), hurtEntity.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, hurtEntity.getSoundSource(), 1.0F, 1.0F);
                if (hurtEntity.level() instanceof ServerLevel serverLevel) serverLevel.getChunkSource().broadcastAndSend(attacker, new ClientboundAnimatePacket(hurtEntity, 4));
            }
        }

        if (hurtEntity instanceof Player player) {
            Ability<?> ability = AbilityHandler.INSTANCE.getAbility(player, AbilityHandler.SOUL_SUMMON_NECKLACE_ABILITY);
            if (ability instanceof SoulSummonNecklaceAbility summonNecklaceAbility) {
                summonNecklaceAbility.hurt(player, source, event.getAmount());
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
                    boolean isPrimary = primaryUUID != null && primaryTick == player.tickCount && hurtEntity.getUUID().equals(primaryUUID);
                    if (isPrimary) {
                        chainswordAbility.setPrimaryTick(0);
                        chainswordAbility.setPrimary(null);
                    }
                    boolean shouldUpdate = player.getAttackStrengthScale(0.5F) >= 1 && isPrimary;
                    chainswordAbility.onAttack(event.getEntity(), shouldUpdate);
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
                    event.setAmount(event.getAmount() * multiplier);
                }
            }
        }
    }

    //实体治疗时
    @SubscribeEvent
    public void onLivingEntityHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();
        MobEffectInstance instance = entity.getEffect(EffectInit.ERODE_EFFECT.getHolder().orElseThrow());
        if (instance != null) {
            float newAmount = Mth.clamp(1F - (instance.getAmplifier() + 1) * 0.1F, 0F, 1F);
            event.setAmount(event.getAmount() * newAmount);
        }
    }

    //实体呼吸事件
    @SubscribeEvent
    public void onLivingEntityBreathe(LivingBreatheEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.hasEffect(EffectInit.FRENZY_EFFECT.getHolder().orElseThrow())) {
            event.setCanBreathe(true);
        }
    }

    //实体设置目标时
    //@SubscribeEvent
    //public void onLivingEntityChangeTarget(LivingChangeTargetEvent event) {
    //    LivingEntity entity = event.getEntity();
    //    VertigoCapability.IVertigoCapability capability = HandlerCapability.getCapability(entity, HandlerCapability.MOVING_CONTROLLER_CAPABILITY);
    //    if (capability != null) {
    //        if (event.isCancelable() && entity instanceof Mob && capability.isVertigo()) {
    //            if (event.getOriginalTarget() != null || entity.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET)) {
    //                event.setCanceled(true);
    //            }
    //        }
    //    }
    //}

    //玩家攻击产生暴击时
    //@SubscribeEvent
    //public void onCriticalHit(CriticalHitEvent event) {
    //
    //}

    //当弹射物触及到物体时
    // Forge kept ProjectileImpactEvent.ImpactResult (verified with javap: DEFAULT, SKIP_ENTITY,
    // STOP_AT_CURRENT, STOP_AT_CURRENT_NO_DAMAGE) and the event is NOT cancelable, so calling setCanceled
    // on it throws at runtime -- that is a NeoForge-only shape that came back with the port and had to be
    // undone. setSecondsOnFire(int) is still renamed to igniteForSeconds(float); that part is a vanilla
    // 1.21 change and stays.
    @SubscribeEvent
    public void onProjectileImpact(ProjectileImpactEvent event) {
        Projectile projectile = event.getProjectile();
        if (event.getRayTraceResult() instanceof EntityHitResult hitResult) {
            if (projectile instanceof EntityBloodBall bloodBall && !bloodBall.isHeal() && hitResult.getEntity() instanceof EntityCorpseWarlock) {
                event.setImpactResult(ProjectileImpactEvent.ImpactResult.SKIP_ENTITY);
            }
            if (projectile instanceof AbstractArrow arrow) {
                if (arrow.getPierceLevel() == 0 && !arrow.fireImmune() && !arrow.isOnFire() && hitResult.getEntity() instanceof EntityImmortalExecutioner) {
                    arrow.igniteForSeconds(100F);
                }
                if (arrow.getOwner() instanceof EntityAbsImmortal immortal1 && hitResult.getEntity() instanceof EntityAbsImmortal immortal2 && immortal1.isAlliedTo(immortal2)) {
                    event.setImpactResult(ProjectileImpactEvent.ImpactResult.SKIP_ENTITY);
                }
            }
        }
    }

    private static float calculateThreatDamage(ItemStack stack, Player player) {
        // ItemStack#isEdible()/getFoodProperties(Player) and PotionUtils were both removed; food is now the
        // DataComponents.FOOD component (a record: nutrition()/saturation()/effects()), and potion effects
        // live on DataComponents.POTION_CONTENTS (see net.minecraft.world.item.alchemy.PotionContents).
        FoodProperties food = stack.get(DataComponents.FOOD);
        if (food != null) {
            float i = food.nutrition() + food.saturation();
            if (!food.effects().isEmpty()) i *= 1.5F;
            return i;
        }
        if (stack.getItem() instanceof PotionItem) {
            PotionContents potionContents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            if (!com.google.common.collect.Iterables.isEmpty(potionContents.getAllEffects())) {
                return 15F;
            }
        }
        return 0F;
    }

    private static void updateGuardianCoreStack(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        Ability<?> ability = AbilityHandler.INSTANCE.getAbility(player, AbilityHandler.GUARDIAN_LASER_ABILITY);
        ItemStack itemStack = player.getUseItem();
        if (ability != null) {
            if (!ability.isUsing()) {
                for (ItemStack stack : player.getInventory().items) {
                    if (stack.is(ItemInit.GUARDIAN_CORE.get()))
                        stack.setDamageValue(Math.max(stack.getDamageValue() - 1, 0));
                }
                for (ItemStack stack : player.getInventory().offhand) {
                    if (stack.is(ItemInit.GUARDIAN_CORE.get()))
                        stack.setDamageValue(Math.max(stack.getDamageValue() - 1, 0));
                }
            } else if (itemStack.is(ItemInit.GUARDIAN_CORE.get())) {
                if (itemStack.getDamageValue() + 1 < itemStack.getMaxDamage()) {
                    // hurtAndBreak(int, LivingEntity, Consumer<Item>) was removed; the (int, LivingEntity,
                    // EquipmentSlot) overload already broadcasts the break event for that slot itself.
                    itemStack.hurtAndBreak(1, player, player.getUsedItemHand() == net.minecraft.world.InteractionHand.MAIN_HAND ? net.minecraft.world.entity.EquipmentSlot.MAINHAND : net.minecraft.world.entity.EquipmentSlot.OFFHAND);
                } else {
                    ability.end();
                }
            } else {
                ability.end();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static boolean doCapabilityEffect(MobEffectInstance effectInstance, LivingEntity entity, boolean added, boolean remove) {
        Capability<?> capability = null;
        boolean shouldCancel = false;
        // MobEffectInstance#getEffect() now returns Holder<MobEffect> (vanilla 1.21 change, not Forge itself), so
        // comparison against a RegistryObject uses Holder#is(Holder) rather than reference equality.
        Holder<MobEffect> effect = effectInstance.getEffect();
        if (effect.is(EffectInit.STUN_EFFECT.getHolder().orElseThrow())) {
            capability = CapabilityHandler.STUN_CAPABILITY;
        } else if (effect.is(EffectInit.FRENZY_EFFECT.getHolder().orElseThrow())) {
            shouldCancel = remove;
            capability = CapabilityHandler.FRENZY_CAPABILITY;
        } else if (effect.is(EffectInit.ELECTRIFIED_EFFECT.getHolder().orElseThrow())) {
            capability = CapabilityHandler.ELECTRICITY_CAPABILITY;
        }
        if (shouldCancel) return true;
        if (capability != null) {
            EEEABMobs.NETWORK.send(new ICapabilityMessage(entity, added, capability), PacketDistributor.TRACKING_ENTITY_AND_SELF.with(entity));
            GeneralCapability iCapability = (GeneralCapability) CapabilityHandler.getCapability(entity, (Capability) capability);
            if (iCapability != null) {
                if (added) iCapability.onStart(entity);
                else iCapability.onEnd(entity);
            }
        }
        return false;
    }

    private static void tryTriggerOverloadExplosion(LivingEntity entity) {
        float baseDamage = ModConfigHandler.COMMON.potions.electrifiedConfig1.get().floatValue();
        if (baseDamage > 0F && entity.removeEffect(EffectInit.ELECTRIFIED_EFFECT.getHolder().orElseThrow())) {
            double radians = Math.toRadians(entity.getYRot() + 90);
            EntityOverloadExplode.explode(entity.level(), entity.position().add(Math.cos(radians), entity.getBbHeight() * 0.3, Math.sin(radians)), entity, 2F, baseDamage);
            entity.clearFire();
        }
    }
}
