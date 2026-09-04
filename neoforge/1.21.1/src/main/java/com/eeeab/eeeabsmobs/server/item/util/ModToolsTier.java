package com.eeeab.eeeabsmobs.server.item.util;


import com.eeeab.eeeabsmobs.server.init.ItemInit;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

//工具级别
//挖掘等级在1.21.1中通过方块标签(TagKey<Block>)表达，不再是数字：
//旧的 harvestLevel=4（netherite 级）映射为 BlockTags.INCORRECT_FOR_NETHERITE_TOOL，
//旧的 harvestLevel=2（iron 级）映射为 BlockTags.INCORRECT_FOR_IRON_TOOL。
public enum ModToolsTier implements Tier {
    RELICRON_TIER(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2031, 9.0F, 4.0F, 15, () -> Ingredient.of(ItemInit.ANCIENT_DRIVE_CRYSTAL.get())),
    IMMORTAL_TIER(BlockTags.INCORRECT_FOR_IRON_TOOL, 500, 6.0F, 2.0F, 14, () -> Ingredient.of(ItemInit.IMMORTAL_INGOT.get())),
    GHOST_STEEL_TIER(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 3000, 9.0F, 5.0F, 20, () -> Ingredient.of(ItemInit.GHOST_STEEL_INGOT.get()));

    private final TagKey<Block> incorrectBlocksForDrops;//不能正确挖掘的方块标签(旧挖掘等级的替代)
    private final int maxUses;//最大耐久
    private final float efficiency;//效率
    private final float attackDamage;//攻击力:材料级别攻击力+武器本身攻击力+玩家攻击力
    private final int enchantability;//附魔亲和力
    private final LazyLoadedValue<Ingredient> repairMaterial;//修复材料

    ModToolsTier(TagKey<Block> incorrectBlocksForDropsIn, int maxUsesIn, float efficiencyIn, float attackDamageIn, int enchantabilityIn, Supplier<Ingredient> repairMaterialIn) {
        this.incorrectBlocksForDrops = incorrectBlocksForDropsIn;
        this.maxUses = maxUsesIn;
        this.efficiency = efficiencyIn;
        this.attackDamage = attackDamageIn;
        this.enchantability = enchantabilityIn;
        this.repairMaterial = new LazyLoadedValue<>(repairMaterialIn);
    }

    @Override
    public int getUses() {
        return this.maxUses;
    }

    @Override
    public float getSpeed() {
        return this.efficiency;
    }

    @Override
    public float getAttackDamageBonus() {
        return this.attackDamage;
    }

    @Override
    public TagKey<Block> getIncorrectBlocksForDrops() {
        return this.incorrectBlocksForDrops;
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairMaterial.get();
    }
}
