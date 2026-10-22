package com.eeeab.eeeabsmobs.server.potion;

import com.eeeab.eeeabsmobs.EEEABMobs;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.*;

//护甲无效化
//1.21.1中addAttributeModifier的id参数从String(UUID)改为Identifier
public class ArmorLowerEffect extends ModEffect {
    public ArmorLowerEffect() {
        super(MobEffectCategory.HARMFUL, 7004023, false);
        this.addAttributeModifier(Attributes.ARMOR, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "armor_lower_armor"), -0.2D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        this.addAttributeModifier(Attributes.ARMOR_TOUGHNESS, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "armor_lower_toughness"), -0.2D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }
}
