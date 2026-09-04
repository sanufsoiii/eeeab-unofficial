package com.eeeab.eeeabsmobs.server.integration.curios;

import com.eeeab.eeeabsmobs.server.item.ICuriosItem;
import net.minecraft.world.item.Item;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

/**
 * Curios 物品注册
 * <p>
 * Curios 9.x replaced the old AttachCapabilitiesEvent&lt;ItemStack&gt;-based per-stack capability provider
 * (CuriosApi.createCurioProvider/CuriosCapability.ID_ITEM, both gone - AttachCapabilitiesEvent itself no
 * longer exists in NeoForge) with a single upfront CuriosApi.registerCurio(Item, ICurioItem) call per Item
 * type. ICurioItem is all-default-methods, so any of this mod's ICuriosItem-marked items can share one
 * behaviourless instance.
 *
 * @author EEEAB
 * @version 1.0
 */
public class CuriosItemFactory {
    private static final ICurioItem DEFAULT_CURIO_BEHAVIOUR = new ICurioItem() {
    };

    public static void registerCurioItem(Item item) {
        if (item instanceof ICuriosItem) {
            CuriosApi.registerCurio(item, DEFAULT_CURIO_BEHAVIOUR);
        }
    }
}
