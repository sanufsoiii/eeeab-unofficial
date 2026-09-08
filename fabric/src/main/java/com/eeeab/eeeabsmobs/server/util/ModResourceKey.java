package com.eeeab.eeeabsmobs.server.util;

import com.eeeab.eeeabsmobs.EEEABMobs;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.Structure;

public class ModResourceKey {
    public static ResourceKey<Structure> COREFORGE_RUINS = registerResourceKey(Registries.STRUCTURE, "coreforge_ruins");
    public static ResourceKey<Structure> BLOODY_ALTAR = registerResourceKey(Registries.STRUCTURE, "bloody_altar");

    public static final ResourceKey<DamageType> BYPASS_ARMOR = registerResourceKey(Registries.DAMAGE_TYPE, "bypass_armor");
    public static final ResourceKey<DamageType> BYPASS_SHIELD = registerResourceKey(Registries.DAMAGE_TYPE, "bypass_shield");
    public static final ResourceKey<DamageType> BYPASS_IFRAME = registerResourceKey(Registries.DAMAGE_TYPE, "bypass_iframe");
    public static final ResourceKey<DamageType> OVERLOAD_EXPLODE = registerResourceKey(Registries.DAMAGE_TYPE, "overload_explode");
    public static final ResourceKey<DamageType> SURGE = registerResourceKey(Registries.DAMAGE_TYPE, "surge");
    public static final ResourceKey<DamageType> ROBUST_ATTACK = registerResourceKey(Registries.DAMAGE_TYPE, "guardian_robust_attack");
    public static final ResourceKey<DamageType> IMMORTAL_MAGIC = registerResourceKey(Registries.DAMAGE_TYPE, "immortal_magic");

    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_ANCIENT_BOUNDARY_STONE = registerResourceKey(Registries.CONFIGURED_FEATURE, "ore_ancient_boundary_stone");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BLIGHTED_OAK = registerResourceKey(Registries.CONFIGURED_FEATURE, "blighted_oak");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_DARKENED_COAL = registerResourceKey(Registries.CONFIGURED_FEATURE, "ore_darkened_coal");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_DARKENED_IRON = registerResourceKey(Registries.CONFIGURED_FEATURE, "ore_darkened_iron");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_VOIDSHARD = registerResourceKey(Registries.CONFIGURED_FEATURE, "ore_voidshard");

    public static final ResourceKey<PlacedFeature> ANCIENT_BOUNDARY_STONE_CHECKED = registerResourceKey(Registries.PLACED_FEATURE, "ancient_boundary_stone_checked");
    public static final ResourceKey<PlacedFeature> SPARSE_BLIGHTED_OAK_CHECKED = registerResourceKey(Registries.PLACED_FEATURE, "blighted_oak_checked");
    public static final ResourceKey<PlacedFeature> DARKENED_COAL_ORE_CHECKED = registerResourceKey(Registries.PLACED_FEATURE, "darkened_coal_ore_checked");
    public static final ResourceKey<PlacedFeature> DARKENED_IRON_ORE_CHECKED = registerResourceKey(Registries.PLACED_FEATURE, "darkened_iron_ore_checked");
    public static final ResourceKey<PlacedFeature> VOIDSHARD_CHECKED = registerResourceKey(Registries.PLACED_FEATURE, "voidshard_checked");

    // BiomeModifier / NeoForgeRegistries.Keys.BIOME_MODIFIERS is a NeoForge-only registry (backed by
    // data/eeeabsmobs/neoforge/biome_modifier/*.json, which Fabric ignores entirely) with no Fabric registry
    // counterpart. Fabric's equivalent - fabric-biome-api-v1's BiomeModifications, which works by predicate +
    // GenerationStep.Decoration + PlacedFeature key rather than a registered ResourceKey<BiomeModifier> - is
    // wired directly in ServerProxy#initWorldgen(); see that method for what still generates.

    public static final ResourceKey<PoiType> EROSION_PORTAL = registerResourceKey(Registries.POINT_OF_INTEREST_TYPE, "erosion_portal");
    public static final ResourceKey<Biome> PRINCIPAL_ISLAND_BIOME = registerResourceKey(Registries.BIOME, "principal_island_biome");
    public static final ResourceKey<Level> VOID_CRACK_LEVEL = registerResourceKey(Registries.DIMENSION, "void_crack");
    public static final ResourceKey<LevelStem> VOID_CRACK_STEM = registerResourceKey(Registries.LEVEL_STEM, "void_crack");
    public static final ResourceKey<DimensionType> VOID_CRACK_TYPE = registerResourceKey(Registries.DIMENSION_TYPE, "void_crack");

    public static final ResourceKey<NoiseGeneratorSettings> VOID_CRACK_NOISE_SETTING = registerResourceKey(Registries.NOISE_SETTINGS, "void_crack_noise_setting");
    public static final ResourceKey<DensityFunction> BASE_3D_NOISE = registerResourceKey(Registries.DENSITY_FUNCTION, "void_crack/base_3d_noise");
    public static final ResourceKey<DensityFunction> SLOPED_CHEESE_VOID_CRACK = registerResourceKey(Registries.DENSITY_FUNCTION, "void_crack/sloped_cheese");

    // RecordItem was removed - discs now point at a data-driven net.minecraft.world.item.JukeboxSong registry
    // entry via Item.Properties#jukeboxPlayable(ResourceKey<JukeboxSong>). The entries themselves live in
    // src/main/resources/data/eeeabsmobs/jukebox_song/*.json (see vanilla's data/minecraft/jukebox_song/*.json
    // for the schema: sound_event, description, length_in_seconds, comparator_output).
    public static final ResourceKey<net.minecraft.world.item.JukeboxSong> NAMELESS_GUARDIAN_THEME_SONG = registerResourceKey(Registries.JUKEBOX_SONG, "nameless_guardian_theme_music_disc");
    public static final ResourceKey<net.minecraft.world.item.JukeboxSong> REALM_WARDEN_THEME_SONG = registerResourceKey(Registries.JUKEBOX_SONG, "realm_warden_theme_music_disc");
    public static final ResourceKey<net.minecraft.world.item.JukeboxSong> THE_IMMORTAL_THEME_SONG = registerResourceKey(Registries.JUKEBOX_SONG, "the_immortal_theme_music_disc");

    private static <T> ResourceKey<T> registerResourceKey(ResourceKey<Registry<T>> registry, String key) {
        return ResourceKey.create(registry, Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, key));
    }
}
