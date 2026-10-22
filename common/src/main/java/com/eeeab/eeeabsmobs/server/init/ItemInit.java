package com.eeeab.eeeabsmobs.server.init;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.server.item.*;
import com.eeeab.eeeabsmobs.server.item.ItemGhostWarriorArmor;
import com.eeeab.eeeabsmobs.server.item.eye.ItemCoreforgeRuinsEye;
import com.eeeab.eeeabsmobs.server.item.eye.ItemBloodyAltarEye;
import com.eeeab.eeeabsmobs.server.item.util.ModSmithingTemplate;
import com.eeeab.eeeabsmobs.server.item.util.ModToolsTier;
import com.eeeab.eeeabsmobs.server.util.ModResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.*;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.registries.BuiltInRegistries;

public class ItemInit {
    public static final FabricDeferredRegister<Item> ITEMS = FabricDeferredRegister.create(BuiltInRegistries.ITEM, EEEABMobs.MOD_ID);

    private static <V extends Item> FabricDeferredRegister.Entry<V> reg(String name, java.util.function.Function<Item.Properties, V> factory) {
        net.minecraft.resources.ResourceKey<Item> key = net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.fromNamespaceAndPath(EEEABMobs.MOD_ID, name));
        return ITEMS.register(name, () -> factory.apply(new Item.Properties().setId(key)));
    }


    //刷怪蛋
    public static final FabricDeferredRegister.Entry<Item> RELIC_OBSERVER_EGG = registerEgg("relic_observer_egg", EntityInit.RELIC_OBSERVER, rgb2dec(35, 35, 35), rgb2dec(113, 113, 113));
    public static final FabricDeferredRegister.Entry<Item> RELIC_RIPPER_EGG = registerEgg("relic_ripper_egg", EntityInit.RELIC_RIPPER, rgb2dec(126, 126, 126), rgb2dec(59, 59, 59));
    public static final FabricDeferredRegister.Entry<Item> RELIC_EARTHSHAKER_EGG = registerEgg("relic_earthshaker_egg", EntityInit.RELIC_EARTHSHAKER, rgb2dec(35, 35, 35), rgb2dec(43, 101, 131));
    public static final FabricDeferredRegister.Entry<Item> RELIC_ANNIHILATOR_EGG = registerEgg("relic_annihilator_egg", EntityInit.RELIC_ANNIHILATOR, rgb2dec(48, 48, 48), rgb2dec(59, 132, 167));
    public static final FabricDeferredRegister.Entry<Item> REALM_WARDEN_EGG = registerEgg("realm_warden_egg", EntityInit.REALM_WARDEN, rgb2dec(48, 48, 48), rgb2dec(162, 162, 162));
    public static final FabricDeferredRegister.Entry<Item> NAMELESS_GUARDIAN_EGG = registerEgg("nameless_guardian_egg", EntityInit.NAMELESS_GUARDIAN, rgb2dec(15, 15, 15), rgb2dec(100, 100, 105));

    public static final FabricDeferredRegister.Entry<Item> CORPSE_EGG = registerEgg("corpse_egg", EntityInit.CORPSE, rgb2dec(28, 47, 49), rgb2dec(72, 135, 115));
    public static final FabricDeferredRegister.Entry<Item> CORPSE_VILLAGER_EGG = registerEgg("corpse_villager_egg", EntityInit.CORPSE_VILLAGER, rgb2dec(28, 47, 49), rgb2dec(72, 129, 125));
    public static final FabricDeferredRegister.Entry<Item> CORPSE_WARLOCK_EGG = registerEgg("corpse_warlock_egg", EntityInit.CORPSE_WARLOCK, rgb2dec(49, 47, 50), rgb2dec(222, 16, 16));
    public static final FabricDeferredRegister.Entry<Item> TESTER_EGG = registerEgg("tester_egg", EntityInit.TESTER, rgb2dec(70, 145, 190), rgb2dec(95, 195, 255));

    public static final FabricDeferredRegister.Entry<Item> IMMORTAL_SKELETON_EGG = registerEgg("immortal_skeleton_egg", EntityInit.IMMORTAL_SKELETON, rgb2dec(147, 147, 143), rgb2dec(83, 83, 77));
    public static final FabricDeferredRegister.Entry<Item> IMMORTAL_ARCHER_EGG = registerEgg("immortal_archer_egg", EntityInit.IMMORTAL_ARCHER, rgb2dec(62, 53, 43), rgb2dec(131, 136, 135));
    public static final FabricDeferredRegister.Entry<Item> IMMORTAL_KNIGHT_EGG = registerEgg("immortal_knight_egg", EntityInit.IMMORTAL_KNIGHT, rgb2dec(30, 41, 47), rgb2dec(151, 155, 154));
    public static final FabricDeferredRegister.Entry<Item> IMMORTAL_MAGE_EGG = registerEgg("immortal_mage_egg", EntityInit.IMMORTAL_MAGE, rgb2dec(97, 108, 116), rgb2dec(46, 54, 59));
    public static final FabricDeferredRegister.Entry<Item> IMMORTAL_WARRIOR_EGG = registerEgg("immortal_warrior_egg", EntityInit.IMMORTAL_WARRIOR, rgb2dec(75, 81, 84), rgb2dec(151, 155, 154));
    public static final FabricDeferredRegister.Entry<Item> IMMORTAL_SHAMAN_EGG = registerEgg("immortal_shaman_egg", EntityInit.IMMORTAL_SHAMAN, rgb2dec(117, 143, 146), rgb2dec(56, 67, 69));
    public static final FabricDeferredRegister.Entry<Item> IMMORTAL_GOLEM_EGG = registerEgg("immortal_golem_egg", EntityInit.MAGIC_GOLEM, rgb2dec(150, 163, 143), rgb2dec(90, 97, 84));
    public static final FabricDeferredRegister.Entry<Item> IMMORTAL_EXECUTIONER_EGG = registerEgg("immortal_executioner_egg", EntityInit.IMMORTAL_EXECUTIONER, rgb2dec(32, 35, 36), rgb2dec(131, 151, 161));
    public static final FabricDeferredRegister.Entry<Item> IMMORTAL_BOSS_EGG = registerEgg("immortal_boss_egg", EntityInit.IMMORTAL_BOSS, rgb2dec(16, 37, 51), rgb2dec(116, 175, 216));

    //物品
    public static final FabricDeferredRegister.Entry<Item> LOGO_ITEM = reg("logo_item", ItemLogo::new);
    public static final FabricDeferredRegister.Entry<Item> REMOVE_MOB = reg("remove_mob", ItemRemoveMob::new);
    public static final FabricDeferredRegister.Entry<Item> ANIMATION_CONTROLLER = reg("animation_controller", ItemAnimationController::new);
    public static final FabricDeferredRegister.Entry<Item> BLOODY_ALTAR_EYE = reg("bloody_altar_eye", ItemBloodyAltarEye::new);
    public static final FabricDeferredRegister.Entry<Item> HEART_OF_PAGAN = reg("heart_of_pagan", p -> new ItemHeartOfPagan(p.stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));
    public static final FabricDeferredRegister.Entry<Item> SOUL_SUMMON_NECKLACE = reg("soul_summon_necklace", p -> new ItemSoulSummonNecklace(p.stacksTo(1).rarity(Rarity.RARE).fireResistant()));
    public static final FabricDeferredRegister.Entry<Item> COREFORGE_RUINS_EYE = reg("coreforge_ruins_eye", ItemCoreforgeRuinsEye::new);
    public static final FabricDeferredRegister.Entry<Item> ANCIENT_DRIVE_CRYSTAL = reg("ancient_drive_crystal", p -> new Item(p.rarity(Rarity.UNCOMMON).fireResistant()));
    public static final FabricDeferredRegister.Entry<Item> BOUNDARY_BRICK = reg("boundary_brick", p -> new Item(p));
    public static final FabricDeferredRegister.Entry<Item> CHAIN_GEAR = reg("chain_gear", p -> new ItemSlidingDoorKey(p.rarity(Rarity.EPIC).fireResistant(), 1));
    public static final FabricDeferredRegister.Entry<Item> GUARDIAN_CUBE = reg("guardian_cube", p -> new ItemSlidingDoorKey(p.rarity(Rarity.EPIC).fireResistant(), 2));
    public static final FabricDeferredRegister.Entry<Item> GUARDIAN_CORE = reg("guardian_core", ItemGuardianCore::new);
    public static final FabricDeferredRegister.Entry<ItemBusterGauntlet> BUSTER_GAUNTLET = reg("buster_gauntlet", p -> new ItemBusterGauntlet(ModToolsTier.RELICRON_TIER, p.rarity(Rarity.RARE).fireResistant()));
    public static final FabricDeferredRegister.Entry<ItemChainsword> CHAINSWORD = reg("chainsword", p -> new ItemChainsword(ModToolsTier.RELICRON_TIER, p.rarity(Rarity.EPIC).fireResistant()));
    public static final FabricDeferredRegister.Entry<ItemSkyfallHammer> SKYFALL_HAMMER = reg("skyfall_hammer", p -> new ItemSkyfallHammer(ModToolsTier.RELICRON_TIER, p.rarity(Rarity.EPIC).fireResistant()));
    public static final FabricDeferredRegister.Entry<ItemDoomboltAxe> DOOMBOLT_AXE = reg("doombolt_battleaxe", p -> new ItemDoomboltAxe(ModToolsTier.RELICRON_TIER, p.rarity(Rarity.EPIC).fireResistant()));
    public static final FabricDeferredRegister.Entry<ItemGuardianAxe> GUARDIAN_AXE = reg("guardian_axe", p -> new ItemGuardianAxe(ModToolsTier.RELICRON_TIER, p.rarity(Rarity.EPIC).fireResistant()));
    public static final FabricDeferredRegister.Entry<Item> RELICRON_UPGRADE_SMITHING_TEMPLATE = reg("relicron_upgrade_smithing_template", ModSmithingTemplate::createRelicronUpgradeTemplate);
    public static final FabricDeferredRegister.Entry<Item> IMMORTAL_BONE = reg("immortal_bone", p -> new Item(p));
    public static final FabricDeferredRegister.Entry<Item> IMMORTAL_STAFF = reg("immortal_staff", ItemImmortalStaff::new);
    public static final FabricDeferredRegister.Entry<Item> IMMORTAL_DEBRIS = reg("immortal_debris", p -> new Item(p));
    public static final FabricDeferredRegister.Entry<Item> IMMORTAL_INGOT = reg("immortal_ingot", p -> new Item(p));
    public static final FabricDeferredRegister.Entry<Item> GHOST_STEEL_INGOT = reg("ghost_steel_ingot", p -> new Item(p.rarity(Rarity.EPIC).fireResistant()));
    public static final FabricDeferredRegister.Entry<Item> GHOST_WARRIOR_HELMET = reg("ghost_warrior_helmet", p -> new ItemGhostWarriorArmor(ArmorType.HELMET, p));
    public static final FabricDeferredRegister.Entry<Item> GHOST_WARRIOR_CHESTPLATE = reg("ghost_warrior_chestplate", p -> new ItemGhostWarriorArmor(ArmorType.CHESTPLATE, p));
    public static final FabricDeferredRegister.Entry<Item> GHOST_WARRIOR_BOOTS = reg("ghost_warrior_boots", p -> new ItemGhostWarriorArmor(ArmorType.BOOTS, p));
    public static final FabricDeferredRegister.Entry<Item> GHOST_WARRIOR_LEGGINGS = reg("ghost_warrior_leggings", p -> new ItemGhostWarriorArmor(ArmorType.LEGGINGS, p));
    public static final FabricDeferredRegister.Entry<Item> GHOST_WARRIOR_UPGRADE_SMITHING_TEMPLATE = reg("ghost_warrior_upgrade_smithing_template", ModSmithingTemplate::createGhostWarriorUpgradeTemplate);
    public static final FabricDeferredRegister.Entry<Item> IMMORTAL_AXE = reg("immortal_axe", p -> new Item(p.axe(ModToolsTier.IMMORTAL_TIER, 6F, -3.1F)));
    public static final FabricDeferredRegister.Entry<Item> IMMORTAL_SWORD = reg("immortal_sword", p -> new ItemImmortalSword(ModToolsTier.IMMORTAL_TIER, 4, -2.4F, p));
    public static final FabricDeferredRegister.Entry<ItemNetherworldKatana> THE_NETHERWORLD_KATANA = reg("netherworld_katana", p -> new ItemNetherworldKatana(ModToolsTier.GHOST_STEEL_TIER, p.rarity(Rarity.EPIC).fireResistant()));

    //唱片
    // RecordItem was removed in 1.21 - discs are now plain Items whose Item.Properties#jukeboxPlayable(...)
    // points at a data-driven JukeboxSong registry entry (src/main/resources/data/eeeabsmobs/jukebox_song/*.json).
    // SoundInit.NAMELESS_GUARDIAN_THEME etc. are no longer referenced directly here - the JSON's sound_event
    // field points at the same eeeabsmobs:music.* sound events instead.
    public static final FabricDeferredRegister.Entry<Item> GUARDIANS_MUSIC_DISC = reg("nameless_guardian_theme_music_disc", p -> new Item(p.stacksTo(1).rarity(Rarity.RARE).fireResistant().jukeboxPlayable(ModResourceKey.NAMELESS_GUARDIAN_THEME_SONG)));
    public static final FabricDeferredRegister.Entry<Item> THE_ONLY_EDICT_MUSIC_DISC = reg("realm_warden_theme_music_disc", p -> new Item(p.stacksTo(1).rarity(Rarity.RARE).fireResistant().jukeboxPlayable(ModResourceKey.REALM_WARDEN_THEME_SONG)));
    public static final FabricDeferredRegister.Entry<Item> THE_ARMY_OF_MINOTAUR_MUSIC_DISC = reg("the_immortal_theme_music_disc", p -> new Item(p.stacksTo(1).rarity(Rarity.RARE).fireResistant().jukeboxPlayable(ModResourceKey.THE_IMMORTAL_THEME_SONG)));

    // NeoForge's DeferredSpawnEggItem lazily resolves its DeferredHolder<EntityType<?>,...> - vanilla's
    // SpawnEggItem just takes the EntityType directly (javap: SpawnEggItem(EntityType<? extends Mob>, int,
    // int, Item.Properties)). That's safe here because EggObject.get() forces EntityInit's static
    // initializer (and therefore all entity-type registration) to run first, per normal JVM class-init order.
    public static FabricDeferredRegister.Entry<Item> registerEgg(String name, FabricDeferredRegister.Entry<? extends EntityType<? extends Mob>> EggObject, int backgroundColor, int highlightColor) {
        // 1.21.9: SpawnEggItem(Properties); entity type bound via Properties#spawnEgg, egg colors moved to the
        // client item model (assets .../items/*_spawn_egg.json), no longer passed in code.
        return reg(name, p -> new SpawnEggItem(p.spawnEgg(EggObject.get())));
    }

    public static ItemStack findBlockItemToStack(FabricDeferredRegister.Entry<Block> block) {
        return block.get().asItem().getDefaultInstance();
    }

    private static int rgb2dec(int r, int g, int b) {
        return (r << 16) | (g << 8) | b;
    }

    public static void register(Object bus) {
        ITEMS.register(bus);
    }

    public static void initializeAttributes() {
        BUSTER_GAUNTLET.get().refreshAttributesFromConfig();
        CHAINSWORD.get().refreshAttributesFromConfig();
        DOOMBOLT_AXE.get().refreshAttributesFromConfig();
        GUARDIAN_AXE.get().refreshAttributesFromConfig();
        //THE_NETHERWORLD_KATANA.get().refreshAttributesFromConfig();
    }
}
