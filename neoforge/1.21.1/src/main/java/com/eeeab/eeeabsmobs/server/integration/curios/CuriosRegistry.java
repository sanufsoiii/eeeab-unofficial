package com.eeeab.eeeabsmobs.server.integration.curios;

import com.eeeab.eeeabsmobs.server.init.ItemInit;
import com.eeeab.eeeabsmobs.server.integration.curios.render.RenderSoulSummoningNecklace;
import net.neoforged.fml.ModList;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

/**
 * Curios API模组兼容
 *
 * @author EEEAB
 * @version 1.0
 */
public final class CuriosRegistry {
    private static final String MOD_ID = "curios";
    private static CuriosRegistry instance;

    public static CuriosRegistry getInstance() {
        if (ModList.get().isLoaded(MOD_ID)) {
            if (instance == null) instance = new CuriosRegistry();
            return instance;
        }
        return null;
    }

    public static void init() {
        var ins = getInstance();
        if (ins == null) return;
        ins.registerImpl();
    }

    private void registerImpl() {
        CuriosItemFactory.registerCurioItem(ItemInit.SOUL_SUMMON_NECKLACE.get());
    }

    public static void initClient() {
        var ins = getInstance();
        if (ins == null) return;
        ins.clientRegisterImpl();
    }

    private void clientRegisterImpl() {
        CuriosRendererRegistry.register(ItemInit.SOUL_SUMMON_NECKLACE.get(), RenderSoulSummoningNecklace::new);
    }
}
