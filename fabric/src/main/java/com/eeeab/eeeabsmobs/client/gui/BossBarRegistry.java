package com.eeeab.eeeabsmobs.client.gui;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.server.init.EntityInit;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Environment(EnvType.CLIENT)
public class BossBarRegistry {
    private static final Map<Identifier, BossBarConfig> STATIC_CONFIGS = new ConcurrentHashMap<>();
    private static final Map<Identifier, BossBarConfig> RESOURCEPACK_OVERRIDES = new ConcurrentHashMap<>();
    private static final Set<Identifier> SCANNED_ENTITIES = ConcurrentHashMap.newKeySet();
    private static final BossBarConfig DEFAULT_CONFIG = new BossBarConfig(
            Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/gui/boss_bar/default_base.png"),
            Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/gui/boss_bar/default_overlay.png"),
            6, 12, 3, -4, -5, 256, 16, 23, ChatFormatting.WHITE);

    /**
     * 初始化注册表
     */
    public static void init() {
        clear();
        // No EntityInit.DEATHPACT_HIEROPHANT exists - checked back to the pristine 1.20.1 import commit
        // (8d31a03) and there never was a Java entity class for it, only these two boss-bar textures. Left
        // unregistered rather than fabricating an entity type; wire this up if/when that entity gets added.
        // BossBarConfig deathpactHierophantConfig = new BossBarConfig(
        //         Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/gui/boss_bar/deathpact_hierophant_base.png"),
        //         Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/gui/boss_bar/deathpact_hierophant_overlay.png"),
        //         6, 12, 3, -4, -5, 256, 16, 23, ChatFormatting.RED);
        // registerSpecialBoss(EntityInit.DEATHPACT_HIEROPHANT.getId(), deathpactHierophantConfig);
        BossBarConfig relicronConfig = new BossBarConfig(
                Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/gui/boss_bar/relicron_base.png"),
                Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, "textures/gui/boss_bar/relicron_overlay.png"),
                4, 8, 4, -8, -6, 256, 16, 23, ChatFormatting.WHITE);
        registerSpecialBoss(EntityInit.RELIC_ANNIHILATOR.getId(), relicronConfig);
        registerSpecialBoss(EntityInit.REALM_WARDEN.getId(), relicronConfig);
    }

    /**
     * 注册特殊Boss配置
     */
    public static void registerSpecialBoss(Identifier entityId, BossBarConfig config) {
        STATIC_CONFIGS.put(entityId, config);
    }

    /**
     * 懒加载获取Boss血条配置
     * 优先级：资源包覆盖 > 静态配置 > 默认配置
     */
    public static BossBarConfig getBarForEntity(Identifier entityId) {
        BossBarConfig resourcePackConfig = RESOURCEPACK_OVERRIDES.get(entityId);
        if (resourcePackConfig != null) {
            return resourcePackConfig;
        }
        if (!SCANNED_ENTITIES.contains(entityId)) {
            SCANNED_ENTITIES.add(entityId);
            Identifier customBase = findTextureInResourcePacks(entityId, "base");
            Identifier customOverlay = findTextureInResourcePacks(entityId, "overlay");
            if (customBase != null || customOverlay != null) {
                BossBarConfig customConfig = getConfig(customBase, customOverlay, STATIC_CONFIGS.getOrDefault(entityId, DEFAULT_CONFIG));
                RESOURCEPACK_OVERRIDES.put(entityId, customConfig);
                EEEABMobs.LOGGER.debug("The custom boss health bar texture overlay applied: {} [base: {}, overlay: {}]",
                        entityId,
                        customBase != null ? "custom" : "template",
                        customOverlay != null ? "custom" : "template");
                return customConfig;
            }
        }
        return STATIC_CONFIGS.getOrDefault(entityId, DEFAULT_CONFIG);
    }

    private static BossBarConfig getConfig(Identifier base, Identifier overlay, BossBarConfig templateConfig) {
        Identifier finalBaseTexture = (base != null) ?
                base : templateConfig.getBaseTexture();
        Identifier finalOverlayTexture = (overlay != null) ?
                overlay : templateConfig.getOverlayTexture();
        return new BossBarConfig(
                finalBaseTexture,
                finalOverlayTexture,
                templateConfig.getBaseHeight(),
                templateConfig.getBaseTextureHeight(),
                templateConfig.getBaseOffsetY(),
                templateConfig.getOverlayOffsetX(),
                templateConfig.getOverlayOffsetY(),
                templateConfig.getOverlayWidth(),
                templateConfig.getOverlayHeight(),
                templateConfig.getVerticalIncrement(),
                templateConfig.getTextColor()
        );
    }

    /**
     * 查找资源包纹理
     */
    private static Identifier findTextureInResourcePacks(Identifier entityId, String suffix) {
        String texturePath = String.format("textures/gui/boss_bar/%s_%s.png", entityId.getPath(), suffix);
        Identifier targetLocation = Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, texturePath);
        try {
            var resourceManager = Minecraft.getInstance().getResourceManager();
            if (resourceManager.getResource(targetLocation).isPresent()) return targetLocation;
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * 清空缓存
     */
    public static void clear() {
        RESOURCEPACK_OVERRIDES.clear();
        SCANNED_ENTITIES.clear();
        STATIC_CONFIGS.clear();
    }

    /**
     * 重新加载资源包覆盖
     */
    public static void reloadResourcePackOverrides() {
        RESOURCEPACK_OVERRIDES.clear();
        SCANNED_ENTITIES.clear();
        EEEABMobs.LOGGER.debug("The custom boss health bar texture overlay cache has been reloaded");
    }
}