package com.eeeab.eeeabsmobs.server.util;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipe;

/**
 * PotionUtils (setPotion/getPotion, operating on item NBT) was removed; potions are now the
 * DataComponents.POTION_CONTENTS data component (see net.minecraft.world.item.alchemy.PotionContents in the
 * sources jar), and Potion registry references are Holder<Potion> rather than a bare Potion.
 */
public class ModBrewingRecipe extends BrewingRecipe {
    private final Holder<Potion> input;
    private final Item ingredient;
    private final Holder<Potion> output;

    public ModBrewingRecipe(Holder<Potion> input, Item ingredient, Holder<Potion> output) {
        super(Ingredient.of(PotionContents.createItemStack(Items.POTION, input)), Ingredient.of(ingredient.getDefaultInstance()), PotionContents.createItemStack(Items.POTION, output));
        this.input = input;
        this.ingredient = ingredient;
        this.output = output;
    }

    @Override
    public boolean isInput(ItemStack input) {
        PotionContents contents = input.get(DataComponents.POTION_CONTENTS);
        return contents != null && contents.is(this.input);
    }

    @Override
    public boolean isIngredient(ItemStack ingredient) {
        return ingredient.getItem() == this.ingredient;
    }

    @Override
    public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
        if (!(isInput(input) && isIngredient(ingredient))) {
            return ItemStack.EMPTY;
        }
        return PotionContents.createItemStack(input.getItem(), this.output);
    }
}
