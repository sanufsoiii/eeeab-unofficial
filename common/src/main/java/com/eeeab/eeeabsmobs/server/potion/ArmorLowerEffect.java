package com.eeeab.eeeabsmobs.server.potion;

import com.eeeab.eeeabsmobs.EEEABMobs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.*;

//护甲无效化
//1.21.1中addAttributeModifier的id参数从String(UUID)改为ResourceLocation
public class ArmorLowerEffect extends ModEffect {
    public ArmorLowerEffect() {
        super(MobEffectCategory.HARMFUL, 7004023, false);
        this.addAttributeModifier(Attributes.ARMOR, new ResourceLocation(EEEABMobs.MOD_ID, "armor_lower_armor"), -0.2D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.ARMOR_TOUGHNESS, new ResourceLocation(EEEABMobs.MOD_ID, "armor_lower_toughness"), -0.2D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }
}
