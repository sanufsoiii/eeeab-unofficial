package com.eeeab.eeeabsmobs.server.item;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.RecordItem;

import java.util.function.Supplier;

public class ModRecordItem extends RecordItem {
    public ModRecordItem(int comparatorOutput, Supplier<SoundEvent> soundSupplier, Item.Properties properties, int lengthInSeconds) {
        super(comparatorOutput, soundSupplier.get(), properties, lengthInSeconds);
    }
}
