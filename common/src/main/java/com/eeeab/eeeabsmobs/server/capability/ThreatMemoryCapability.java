package com.eeeab.eeeabsmobs.server.capability;

import com.eeeab.eeeabsmobs.EEEABMobs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ThreatMemoryCapability {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "threat_memory_cap");

    public interface IThreatMemoryCapability {
        void recordDamage(LivingEntity self, LivingEntity attacker, float amount);

        LivingEntity getHighestThreat(LivingEntity self);

        void clear();

        boolean isAnyPlayerInvolved();
    }

    public static class IThreatMemoryCapabilityImpl implements IThreatMemoryCapability {
        private static final TargetingConditions HURT_BY_TARGETING = TargetingConditions.forCombat().ignoreInvisibilityTesting();
        private static final int MAX_ANGER_TICK = 1200;
        private static final double THREAT_SCALE = 10;
        private static final double DECAY_RATE = 0.98;
        private final Map<LivingEntity, ThreatRecord> threatMap = new HashMap<>();
        private int playerParticipantCount;
        private boolean fightWithPlayer;

        private static class ThreatRecord {
            double threat;
            int lastHitTick;

            ThreatRecord(double threat, int lastHitTick) {
                this.threat = threat;
                this.lastHitTick = lastHitTick;
            }
        }

        @Override
        public void recordDamage(LivingEntity self, LivingEntity attacker, float amount) {
            if (attacker == null || amount <= 0 || !Double.isFinite(amount)) return;
            if (!attacker.isAlive() || self.level() != attacker.level() || !self.canAttack(attacker)) return;
            double threatIncrement = Math.log1p(amount) * THREAT_SCALE;
            int now = self.tickCount;
            ThreatRecord record = threatMap.get(attacker);
            double existingThreat = 0.0;
            if (record != null) {
                int tickDiff = now - record.lastHitTick;
                if (tickDiff > 0 && tickDiff < MAX_ANGER_TICK) {
                    existingThreat = record.threat * Math.pow(DECAY_RATE, tickDiff);
                }
            }
            boolean isNewAttacker = !threatMap.containsKey(attacker);
            boolean isPlayerRelated = isPlayerOrPet(attacker, true);
            if (isNewAttacker && isPlayerRelated) {
                playerParticipantCount++;
                fightWithPlayer = true;
            }
            double newThreat = existingThreat + threatIncrement;
            threatMap.put(attacker, new ThreatRecord(newThreat, now));
        }

        @Override
        public LivingEntity getHighestThreat(LivingEntity self) {
            if (self == null) return null;

            int currentTick = self.tickCount;
            Map<LivingEntity, Double> validEntries = new HashMap<>();

            Iterator<Map.Entry<LivingEntity, ThreatRecord>> iterator = threatMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<LivingEntity, ThreatRecord> entry = iterator.next();
                LivingEntity attacker = entry.getKey();
                ThreatRecord record = entry.getValue();
                if (attacker == null || !attacker.isAlive() || self.level() != attacker.level() || !self.canAttack(attacker)) {
                    if (isPlayerOrPet(attacker, false)) {
                        playerParticipantCount--;
                    }
                    iterator.remove();
                    continue;
                }
                int tickDiff = currentTick - record.lastHitTick;
                if (tickDiff < 0 || tickDiff > MAX_ANGER_TICK) {
                    if (isPlayerOrPet(attacker, false)) {
                        playerParticipantCount--;
                    }
                    iterator.remove();
                    continue;
                }
                double decayedThreat = Math.max(record.threat * Math.pow(DECAY_RATE, tickDiff), 0.1);
                validEntries.put(attacker, decayedThreat);
            }
            playerParticipantCount = Math.max(0, playerParticipantCount);
            if (validEntries.isEmpty()) {
                fightWithPlayer = false;
                return null;
            }
            return validEntries.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);
        }

        @Override
        public void clear() {
            threatMap.clear();
            fightWithPlayer = false;
            playerParticipantCount = 0;
        }

        @Override
        public boolean isAnyPlayerInvolved() {
            return playerParticipantCount > 0 || fightWithPlayer;
        }

        private boolean isPlayerOrPet(LivingEntity entity, boolean requireSurvival) {
            if (entity instanceof Player player) {
                if (requireSurvival) return !player.isCreative() && !player.isSpectator();
                return true;
            }
            if (entity instanceof OwnableEntity ownable) {
                if (ownable.getOwner() instanceof Player player) {
                    if (requireSurvival) return !player.isCreative() && !player.isSpectator();
                    return true;
                }
            }
            return entity instanceof IronGolem ironGolem && ironGolem.isPlayerCreated();
        }
    }
}
