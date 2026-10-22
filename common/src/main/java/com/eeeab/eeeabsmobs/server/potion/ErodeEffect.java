package com.eeeab.eeeabsmobs.server.potion;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.server.init.AttributeInit;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

//侵蚀
//1.21.1中addAttributeModifier的id参数从String(UUID)改为Identifier；
//AttributeInit.CRIT_CHANCE本身是DeferredHolder<Attribute,Attribute>，实现Holder<Attribute>，直接传递而不用.get()
public class ErodeEffect extends ModEffect {
    public ErodeEffect() {
        super(MobEffectCategory.HARMFUL, 1648179, false);
        this.addAttributeModifier(AttributeInit.CRIT_CHANCE.holder(), ResourceLocation.fromNamespaceAndPath(EEEABMobs.MOD_ID, "erode_crit_chance"), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    //@Override
    //protected boolean canApplyEffect(int remainingTicks, int level) {
    //    return remainingTicks % 15 == 0;
    //}
    //
    //@Override
    //public void applyEffectTick(LivingEntity entity, int amplified) {
    //    amplified = amplified + 1;
    //    int i = random.nextInt(3);
    //    if (entity instanceof Player player && (player.isCreative() || player.isSpectator())) return;
    //    if (i == 0) {
    //        for (EquipmentSlot slot : EquipmentSlot.values()) {
    //            if (slot.getType() == EquipmentSlot.Type.ARMOR) {
    //                damageArmor(slot, entity, amplified);
    //            }
    //        }
    //    }
    //}
    //
    //private void damageArmor(EquipmentSlot slot, LivingEntity entity, int amplified) {
    //    ItemStack stack = entity.getItemBySlot(slot);
    //    if (stack.is(EMTagKey.EROSION_IMMUNE_ITEMS)) return;
    //    stack.hurtAndBreak(amplified, entity, e -> e.broadcastBreakEvent(slot));
    //}
}

