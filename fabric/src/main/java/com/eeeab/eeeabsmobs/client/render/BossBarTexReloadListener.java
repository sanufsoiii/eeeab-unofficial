package com.eeeab.eeeabsmobs.client.render;

import com.eeeab.eeeabsmobs.client.gui.BossBarRegistry;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Environment(EnvType.CLIENT)
public class BossBarTexReloadListener implements PreparableReloadListener {

    @Override
    public CompletableFuture<Void> reload(PreparableReloadListener.SharedState sharedState, Executor prepareExecutor,
                                          PreparableReloadListener.PreparationBarrier stage, Executor gameExecutor) {

        CompletableFuture<Void> preparation = CompletableFuture.completedFuture(null);
        return preparation.thenCompose(stage::wait).thenRunAsync(BossBarRegistry::reloadResourcePackOverrides, gameExecutor);
    }
}