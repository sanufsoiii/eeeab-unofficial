package com.eeeab.eeeabsmobs.server.handler;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.architectury.platform.Platform;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Locale;

/**
 * Fabric has no counterpart to NeoForge's ModConfigSpec (that was Forge/NeoForge-only, backed by
 * night-config and the {@code @Mod.EventBusSubscriber}/{@code ModLoadingContext#registerConfig} wiring
 * ServerProxy used to do). There is no drop-in replacement library on Fabric, so this is a small,
 * dependency-free-besides-Gson JSON config: {@link ConfigBuilder} mimics ModConfigSpec.Builder's
 * push/pop/comment/translation/define* chain closely enough that the tree of nested classes below
 * (Common, Item, Entity, Mob, ... AttributeConfig, BossConfig, ToolConfig, DamageSourceAdaptConfig,
 * Client, TipNotification) did not need to change shape at all - only the two type names swapped:
 * {@code ModConfigSpec.Builder -> ConfigBuilder} and {@code ModConfigSpec.<Foo>Value -> ConfigValue<Foo>}.
 * Every external accessor the rest of the mod already uses, e.g.
 * {@code ModConfigHandler.COMMON.items.doomboltAxeConfig.get()}, is unchanged.
 * <p>
 * There are two JSON files under the Fabric config directory (FabricLoader#getConfigDir):
 * {@code eeeabsmobs-common.json} and {@code eeeabsmobs-client.json} - the same COMMON/CLIENT split
 * NeoForge used, just as plain files instead of ModConfig.Type registrations. Each ConfigValue records
 * its own dot-joined path (built from the push()/pop() category stack, e.g.
 * "items.doombolt_battleaxe.set_item_cool_down_time") and the file is a flat JSON object keyed by that
 * path - no nested JSON objects, so ConfigBuilder doesn't need a tree-building pass. Loading happens once
 * in the static initializer below, after COMMON and CLIENT (and therefore every ConfigValue) exist:
 * existing keys in the file override the compiled-in defaults, missing keys fall back to the default,
 * and the file is then rewritten so a first run generates it with every default value present and any
 * unknown/stale keys dropped. There is no GUI/translation system behind .translation(...) any more (no
 * NeoForge config screen), so ConfigBuilder#translation is a harmless no-op kept only so the existing
 * builder chains below did not need editing.
 * <p>
 * NOTE for whoever finishes the Fabric proxy: this class self-loads on first static access (constructing
 * COMMON/CLIENT triggers the static initializer, which reads+writes the JSON immediately after). Unlike
 * the old NeoForge path there is nothing left to register from ServerProxy#initMod - the
 * ModLoadingContext.get().getActiveContainer().registerConfig(...) call there should simply be deleted,
 * not replaced with an equivalent call, once that file is converted.
 */
public final class ModConfigHandler {
    public static final ConfigBuilder COMMON_BUILDER = new ConfigBuilder();
    public static final ConfigBuilder CLIENT_BUILDER = new ConfigBuilder();
    public static final Common COMMON;
    public static final Client CLIENT;

    private ModConfigHandler() {
    }

    // Must be declared before the static block below: Java runs static initialisers in source
    // order, and that block calls loadAndSave(), which uses GSON. With the field declared after it,
    // GSON was still null and every item that reads config during construction died at mod init.
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    static {
        COMMON = new Common(COMMON_BUILDER);
        CLIENT = new Client(CLIENT_BUILDER);
        loadAndSave(COMMON_BUILDER, "eeeabsmobs-common.json");
        loadAndSave(CLIENT_BUILDER, "eeeabsmobs-client.json");
    }

    private static String getTranslationKey(String key) {
        // Was TranslateUtils.simpleConfigText(key, null).getString() - that only fed the NeoForge config
        // screen's labels, which no longer exists on this branch (no config GUI has been ported). Keeping
        // the call sites below unchanged (via ConfigBuilder#translation being a no-op) rather than ripping
        // every .translation(getTranslationKey(...)) out, so this file stays a close diff of the original.
        return key;
    }


    private static void loadAndSave(ConfigBuilder builder, String fileName) {
        Path file = Platform.getConfigFolder().resolve(fileName);
        JsonObject existing = new JsonObject();
        if (Files.isRegularFile(file)) {
            try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                JsonElement parsed = JsonParser.parseReader(reader);
                if (parsed != null && parsed.isJsonObject()) {
                    existing = parsed.getAsJsonObject();
                }
            } catch (IOException | RuntimeException e) {
                EEEABMobs.LOGGER.error("Failed to read config file {}, regenerating it with defaults", file, e);
            }
        }
        for (ConfigValue<?> value : builder.values()) {
            value.readFrom(existing);
        }
        JsonObject out = new JsonObject();
        for (ConfigValue<?> value : builder.values()) {
            value.writeTo(out);
        }
        try {
            Files.createDirectories(file.getParent());
            try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(out, writer);
            }
        } catch (IOException e) {
            EEEABMobs.LOGGER.error("Failed to write config file {}", file, e);
        }
    }

    public static class Common {
        public Common(final ConfigBuilder builder) {
            items = new Item(builder);
            entities = new Entity(builder);
            mobs = new Mob(builder);
            potions = new Potion(builder);
            others = new Other(builder);
        }

        public final Mob mobs;
        public final Entity entities;
        public final Item items;
        public final Potion potions;
        public final Other others;
    }

    public static class Item {
        public Item(final ConfigBuilder builder) {
            builder.push("Items");
            {
                builder.push("Eye Of Structure");
                eyeOfStructureConfig1 = builder.comment("If set to 'True' eye of structure will be consume item on release")
                        .translation(getTranslationKey("eye_of_structure"))
                        .define("Consume Item On Release", false);
                eyeOfStructureConfig2 = itemCD(4D);
                builder.pop();
            }

            builder.push("Weapons");
            {
                builder.push("Doombolt Battleaxe");
                doomboltAxeConfig = itemCD(3D);
                doomboltAxe = new ToolConfig(15D, 0.9D);
                builder.push("Surge");
                surge = new AttributeConfig(7.5F);
                builder.pop();
                builder.pop();
            }
            {
                builder.push("Annihilator Sawblade");
                chainsword = new ToolConfig(10D, 1.6D);
                chainswordConfig1 = builder.comment("Set the percentage of damage increase per stack")
                        .translation(getTranslationKey("chainsword_1"))
                        .defineInRange("Set Damage Bonus Per Stack", 5D, 1D, 10D);
                chainswordConfig2 = builder.comment("Set the maximum number of stacks for the damage buff")
                        .translation(getTranslationKey("chainsword_2"))
                        .defineInRange("Set Max Buff Stacks", 5, 0, 10);
                builder.pop();
            }
            {
                builder.push("Guardian Core");
                guardianCoreConfig1 = builder.translation(getTranslationKey("attack_radius"))
                        .defineInRange("Set Attack Radius", 16D, 1D, 64D);
                guardianCoreConfig2 = builder.translation(getTranslationKey("guardian_core"))
                        .define("Enable Break Block", false);
                guardianLaser = new AttributeConfig(5F);
                builder.pop();
            }
            {
                builder.push("Buster Gauntlet");
                busterGauntletConfig1 = builder.translation(getTranslationKey("projectile_damage"))
                        .defineInRange("Set Projectile Damage", 10D, 1D, Float.MAX_VALUE);
                busterGauntletConfig2 = builder.comment("Set Grenade explosion radius")
                        .translation(getTranslationKey("attack_radius"))
                        .defineInRange("Set Attack Radius", 2.5D, 1D, 5D);
                busterGauntletConfig3 = itemCD(4D);
                busterGauntlet = new ToolConfig(8D, 1.6D);
                builder.pop();
            }
            //{
            //    builder.push("Ghost Warrior Series");
            //    ghostWarriorSeriesConfig1 = builder.comment("If set to 'True' armor or weapon will be depleted")
            //            .translation(getTranslationKey("series_depleted"))
            //            .define("Enable Armor Or Weapon Can Be Depleted", false);
            //    netherworldKatana = new ToolConfig(14D, 1.4D);
            //    builder.pop();
            //}
            //{
            //    builder.push("Immortal Staff");
            //    immortalStaffConfig1 = itemCD(3D);
            //    immortalStaffConfig2 = builder.translation(getTranslationKey("projectile_damage"))
            //            .defineInRange("Set Projectile Damage", 8D, 1D, Float.MAX_VALUE);
            //    builder.pop();
            //}
            builder.pop();
            builder.push("Accessories");
            {
                builder.push("Summoning Soul Necklace");
                summoningSoulNecklaceConfig1 = builder.comment("Set maximum amount of damage a player can take while holding this item")
                        .translation(getTranslationKey("summoning_soul_necklace"))
                        .defineInRange("Set Maximum Cumulative Damage Taken", 50D, 1D, Float.MAX_VALUE);
                summoningSoulNecklaceConfig2 = itemCD(20D);
                builder.pop();
            }
            builder.pop();
            builder.pop();
        }

        public final ConfigValue<Boolean> eyeOfStructureConfig1;
        public final ConfigValue<Double> eyeOfStructureConfig2;
        public final ConfigValue<Double> summoningSoulNecklaceConfig1;
        public final ConfigValue<Double> summoningSoulNecklaceConfig2;
        public final ConfigValue<Double> busterGauntletConfig1;
        public final ConfigValue<Double> busterGauntletConfig2;
        public final ConfigValue<Double> busterGauntletConfig3;
        public final ToolConfig busterGauntlet;
        public final ConfigValue<Double> doomboltAxeConfig;
        public final ToolConfig doomboltAxe;
        public final AttributeConfig surge;
        public final ToolConfig chainsword;
        public final ConfigValue<Integer> chainswordConfig2;
        public final ConfigValue<Double> chainswordConfig1;
        public final ConfigValue<Double> guardianCoreConfig1;
        public final ConfigValue<Boolean> guardianCoreConfig2;
        public final AttributeConfig guardianLaser;
        //public final ConfigValue<Double> immortalStaffConfig1;
        //public final ConfigValue<Double> immortalStaffConfig2;
        //public final ConfigValue<Boolean> ghostWarriorSeriesConfig1;
        //public final ToolConfig netherworldKatana;

        private static ConfigValue<Double> itemCD(double defaultCD) {
            String CDComment = "Set item cool down time after player on use (in seconds)";
            String CDPath = "Set Item Cool Down Time";
            String CDKey = "item_cd";
            return COMMON_BUILDER.comment(CDComment)
                    .translation(getTranslationKey(CDKey))
                    .defineInRange(CDPath, defaultCD, 0D, 60D);
        }
    }

    public static class Entity {
        public Entity(final ConfigBuilder builder) {
            builder.push("Entities");
            {
                builder.push("Falling Block");
                fallingBlockConfig1 = builder.comment("If set to 'False', Disable falling block spawn")
                        .translation(getTranslationKey("falling_block_1")).define("Enable Falling Block Spawn", true);
                fallingBlockConfig2 = builder.comment("Set the maximum range for downward block detection to determine the Y-axis position where the entity spawns")
                        .translation(getTranslationKey("falling_block_2")).defineInRange("Set Below Check Range", 3, 1, 10);
                builder.pop();
            }
            {
                builder.push("Tester");
                testerConfig1 = builder.translation(getTranslationKey("tester_1")).defineInRange("Set Max Health", 20D, 1D, 1024D);
                testerConfig2 = builder.translation(getTranslationKey("tester_2")).define("Enable Immune To Environmental Or Status Damage", true);
                builder.pop();
            }
            builder.pop();
        }

        public final ConfigValue<Boolean> fallingBlockConfig1;
        public final ConfigValue<Integer> fallingBlockConfig2;
        public final ConfigValue<Double> testerConfig1;
        public final ConfigValue<Boolean> testerConfig2;
    }

    public static class Mob {
        public Mob(final ConfigBuilder builder) {
            builder.push("Mobs");
            immortals = new ImmortalMobs(builder);
            corpses = new CorpseMobs(builder);
            relicrons = new Relicrons(builder);
            minions = new MinionMobs(builder);
            builder.pop();
        }

        public final CorpseMobs corpses;
        public final ImmortalMobs immortals;
        public final Relicrons relicrons;
        public final MinionMobs minions;
    }

    public static class ImmortalMobs {
        public ImmortalMobs(final ConfigBuilder builder) {
            builder.push("Immortal Mobs");
            immortalSkeleton = new ImmortalSkeleton(builder);
            immortalShaman = new ImmortalShaman(builder);
            immortalGolem = new ImmortalGolem(builder);
            immortalExecutioner = new ImmortalExecutioner(builder);
            immortal = new Immortal(builder);
            builder.pop();
        }

        public final ImmortalSkeleton immortalSkeleton;
        public final ImmortalShaman immortalShaman;
        public final ImmortalGolem immortalGolem;
        public final ImmortalExecutioner immortalExecutioner;
        public final Immortal immortal;
    }

    //不朽骷髅
    public static class ImmortalSkeleton {
        public ImmortalSkeleton(final ConfigBuilder builder) {
            builder.push("Immortal Skeleton");
            combatConfig = new AttributeConfig();
            builder.pop();
        }

        public final AttributeConfig combatConfig;
    }

    //不朽巫师
    public static class ImmortalShaman {
        public ImmortalShaman(final ConfigBuilder builder) {
            builder.push("Immortal Shaman");
            healPercentage = builder.comment("Immortal Shaman heal values (based on max health percentage)")
                    .translation(getTranslationKey("heal_percentage")).defineInRange("Heal Percentage", 0.5D, 0D, 1D);
            combatConfig = new AttributeConfig();
            {
                builder.push("Shaman Bomb").comment("BASE DAMAGE");
                shamanBomb = new AttributeConfig(8F);
                builder.pop();
            }
            builder.pop();
        }

        public final ConfigValue<Double> healPercentage;
        public final AttributeConfig combatConfig;
        public final AttributeConfig shamanBomb;
    }

    //不朽傀儡
    public static class ImmortalGolem {
        public ImmortalGolem(final ConfigBuilder builder) {
            builder.push("Immortal Golem");
            combatConfig = new AttributeConfig();
            builder.pop();
        }

        public final AttributeConfig combatConfig;
    }

    //不朽斩魂者
    public static class ImmortalExecutioner {
        public ImmortalExecutioner(final ConfigBuilder builder) {
            builder.push("Immortal Executioner");
            combatConfig = new AttributeConfig();
            maximumDetonationCount = builder.comment("Set the number of times to strengthen this mob")
                    .translation(getTranslationKey("immortal_executioner")).defineInRange("Ignite Count", 3, 0, 1024);
            builder.pop();
        }

        public final AttributeConfig combatConfig;
        public final ConfigValue<Integer> maximumDetonationCount;
    }

    //不朽
    public static class Immortal {
        public Immortal(final ConfigBuilder builder) {
            builder.push("The Immortal");
            {
                builder.push("Immortal Shuriken").comment("BASE DAMAGE");
                immortalShuriken = new AttributeConfig(5);
                builder.pop();
            }
            {
                builder.push("Immortal Laser").comment("BASE DAMAGE");
                immortalLaser = new AttributeConfig(5);
                builder.pop();
            }
            combatConfig = new AttributeConfig();
            bossConfig = new BossConfig(30D, 16D, 0.25D);
            adaptConfig = new DamageSourceAdaptConfig(15, 60, 1.5D, 2D, 0.9D);
            builder.pop();
        }

        public final AttributeConfig combatConfig;
        public final AttributeConfig immortalShuriken;
        public final AttributeConfig immortalLaser;
        public final BossConfig bossConfig;
        public final DamageSourceAdaptConfig adaptConfig;
    }

    public static class CorpseMobs {
        public CorpseMobs(final ConfigBuilder builder) {
            builder.push("Corpse Mobs");
            corpse = new Corpse(builder);
            corpseWarlock = new CorpseWarlock(builder);
            builder.pop();
        }

        public final Corpse corpse;
        public final CorpseWarlock corpseWarlock;
    }

    //死尸&死尸村民
    public static class Corpse {
        public Corpse(final ConfigBuilder builder) {
            builder.push("Corpse & Corpse Villager");
            combatConfig = new AttributeConfig();
            enableConvertToCorpse = builder.comment("If set to 'False', When zombies or villagers are killed by corpses, they will not converted into corpses themselves")
                    .translation(getTranslationKey("corpse")).define("Converted To Corpse", true);
            builder.pop();
        }

        public final AttributeConfig combatConfig;
        public final ConfigValue<Boolean> enableConvertToCorpse;
    }

    //死尸术士
    public static class CorpseWarlock {
        public CorpseWarlock(final ConfigBuilder builder) {
            builder.push("Corpse Warlock");
            combatConfig = new AttributeConfig();
            {
                builder.push("Crimson Ray");
                crimsonRay = new AttributeConfig(5F);
                builder.pop();
            }
            {
                builder.push("Crimson Crack");
                crimsonCrack = new AttributeConfig(5F);
                builder.pop();
            }
            {
                builder.push("Blood Ball").comment("BASE DAMAGE");
                bloodBall = new AttributeConfig(5F);
                builder.pop();
            }
            builder.pop();
        }

        public final AttributeConfig combatConfig;
        public final AttributeConfig crimsonRay;
        public final AttributeConfig crimsonCrack;
        public final AttributeConfig bloodBall;
    }

    public static class Relicrons {
        public Relicrons(final ConfigBuilder builder) {
            builder.push("Relicrons");
            relicObserver = new RelicObserver(builder);
            relicRipper = new RelicRipper(builder);
            relicEarthshaker = new RelicEarthshaker(builder);
            relicAnnihilator = new RelicAnnihilator(builder);
            realmwarden = new Realmwarden(builder);
            namelessGuardian = new NamelessGuardian(builder);
            outOfCombatHeal = builder.comment("If set to 'False', Disable health regen while out of combat")
                    .translation(getTranslationKey("out_of_combat")).define("Enable Out Of Combat Health Regen", true);
            builder.pop();
        }

        public final RelicObserver relicObserver;
        public final RelicRipper relicRipper;
        public final RelicEarthshaker relicEarthshaker;
        public final RelicAnnihilator relicAnnihilator;
        public final Realmwarden realmwarden;
        public final NamelessGuardian namelessGuardian;
        public final ConfigValue<Boolean> outOfCombatHeal;
    }

    //遗迹观察者
    public static class RelicObserver {
        public RelicObserver(final ConfigBuilder builder) {
            builder.push("Relic Observer");
            combatConfig = new AttributeConfig();
            {
                builder.push("Guardian Laser");
                guardianLaser = new AttributeConfig(8F);
                builder.pop();
            }
            builder.pop();
        }

        public final AttributeConfig combatConfig;
        public final AttributeConfig guardianLaser;
    }

    //遗迹撕裂者
    public static class RelicRipper {
        public RelicRipper(final ConfigBuilder builder) {
            builder.push("Relic Ripper");
            combatConfig = new AttributeConfig();
            builder.pop();
        }

        public final AttributeConfig combatConfig;
    }

    //遗迹撼地者
    public static class RelicEarthshaker {
        public RelicEarthshaker(final ConfigBuilder builder) {
            builder.push("Relic Earthshaker");
            combatConfig = new AttributeConfig();
            {
                builder.push("Electromagnetic");
                electromagnetic = new AttributeConfig(8F);
                builder.pop();
            }
            {
                builder.push("Pulsed Grenade");
                pulsedGrenade = new AttributeConfig(8F);
                builder.pop();
            }
            builder.pop();
        }

        public final AttributeConfig combatConfig;
        public final AttributeConfig electromagnetic;
        public final AttributeConfig pulsedGrenade;
    }

    //遗迹歼击者
    public static class RelicAnnihilator {
        public RelicAnnihilator(final ConfigBuilder builder) {
            builder.push("Relic Annihilator");
            combatConfig = new AttributeConfig();
            bossConfig = new BossConfig(20D, 24D, 0.75D);
            {
                builder.push("Guardian Laser");
                guardianLaser = new AttributeConfig(10F);
                builder.pop();
            }
            {
                builder.push("Annihilator Missile");
                annihilatorMissile = new AttributeConfig(10F);
                builder.pop();
            }
            {
                builder.push("Electromagnetic");
                electromagnetic = new AttributeConfig(10F);
                builder.pop();
            }
            builder.pop();
        }

        public final AttributeConfig combatConfig;
        public final BossConfig bossConfig;
        public final AttributeConfig guardianLaser;
        public final AttributeConfig annihilatorMissile;
        public final AttributeConfig electromagnetic;
    }

    public static class Realmwarden {
        public Realmwarden(final ConfigBuilder builder) {
            builder.push("Realmwarden");
            combatConfig = new AttributeConfig();
            bossConfig = new BossConfig(20D, 14D, 1D);
            {
                builder.push("Annihilator Missile");
                annihilatorMissile = new AttributeConfig(15F);
                builder.pop();
            }
            {
                builder.push("Surge");
                surge = new AttributeConfig(10F);
                builder.pop();
            }
            builder.pop();
        }

        public final AttributeConfig combatConfig;
        public final BossConfig bossConfig;
        public final AttributeConfig annihilatorMissile;
        public final AttributeConfig surge;
    }

    //无名守卫者
    public static class NamelessGuardian {
        public NamelessGuardian(final ConfigBuilder builder) {
            builder.push("Nameless Guardian");
            suckBloodMultiplier = builder.comment("Set suck blood multiplier")
                    .translation(getTranslationKey("suck_blood")).defineInRange("Suck Blood Multiplier", 1D, 0D, 1024D);
            enableForcedSuckBlood = builder.comment("If set to 'False', Disable forced suck blood on power status(This setting does not take effect in Challenge Mode)")
                    .translation(getTranslationKey("forced_suck_blood")).define("Enable Forced Suck Blood", true);
            challengeMode = builder.comment("Be careful! It's going to get tricky!")
                    .translation(getTranslationKey("challenge_mode")).define("Challenge Mode", false);
            combatConfig = new AttributeConfig();
            bossConfig = new BossConfig(20D, 18D, 0.5D);
            {
                builder.push("Guardian Laser");
                guardianLaser = new AttributeConfig(5F);
                builder.pop();
            }
            {
                builder.push("Guardian Blade");
                guardianBlade = new AttributeConfig(15F);
                builder.pop();
            }
            builder.pop();
        }

        //吸血倍率
        public final ConfigValue<Double> suckBloodMultiplier;
        //启用强制吸血
        public final ConfigValue<Boolean> enableForcedSuckBlood;
        //挑战模式
        public final ConfigValue<Boolean> challengeMode;
        public final AttributeConfig combatConfig;
        public final BossConfig bossConfig;
        public final AttributeConfig guardianLaser;
        public final AttributeConfig guardianBlade;
    }

    //召唤类生物
    public static class MinionMobs {
        public MinionMobs(final ConfigBuilder builder) {
            builder.push("Minion Mobs");
            CORPSE_MINION = new CorpseToPlayer(builder);
            builder.pop();
        }

        public final CorpseToPlayer CORPSE_MINION;
    }

    public static class CorpseToPlayer {
        public CorpseToPlayer(final ConfigBuilder builder) {
            builder.push("Corpse Minion");
            combatConfig = new AttributeConfig();
            minionDeathHealAmount = builder.comment("Set corpse minion to restore its owner's health")
                    .translation(getTranslationKey("corpse_to_player")).defineInRange("Death Heal Amount", 5D, 0D, 1024D);
            builder.pop();
        }

        public final AttributeConfig combatConfig;
        public final ConfigValue<Double> minionDeathHealAmount;
    }

    //药水效果
    public static class Potion {
        public Potion(final ConfigBuilder builder) {
            builder.push("Potions");
            {
                builder.push("Electromagnetic Overload");
                electrifiedConfig1 = builder.comment("Base damage dealt by the electromagnetic overload effect")
                        .translation(getTranslationKey("electrified_effect_1"))
                        .defineInRange("Set Base Damage", 8D, 0D, Float.MAX_VALUE);
                electrifiedConfig2 = builder.comment("Additional damage as a percentage of target's max health")
                        .translation(getTranslationKey("electrified_effect_2"))
                        .defineInRange("Set Max Health Ratio", 0.05D, 0D, 0.2D);
                builder.pop();
            }
            builder.pop();
        }

        public final ConfigValue<Double> electrifiedConfig1;
        public final ConfigValue<Double> electrifiedConfig2;
    }

    //其他设置
    public static class Other {
        public Other(final ConfigBuilder builder) {
            builder.push("Others");
            {
                builder.push("Bosses");
                enableShowBossBars = builder.comment("If set to 'False', Disable show boss bar")
                        .translation(getTranslationKey("show_boss_bar")).define("Enable Show Boss Health Bars", true);
                bossBarMaxDist = builder.comment("Set maximum render distance for boss health bar")
                        .translation(getTranslationKey("boss_bar_render_dist")).defineInRange("Set Render Distance For Boss Bars", 48D, 32D, 128D);
                builder.pop();
            }
            enableMobGriefing = builder.comment("If set to 'False', Disable mobs griefing")
                    .translation(getTranslationKey("mob_griefing")).define("Enable Mob Griefing", true);
            enableItemDropsWhenBreakBlocks = builder.comment("If set to 'False', Breaking blocks will not drop items")
                    .translation(getTranslationKey("do_break_loot")).define("Enable Item Drops When Break Blocks", true);
            enableShowItemCD = builder.comment("If set to 'False', Disable showing cooldown time in tooltip")
                    .translation(getTranslationKey("show_item_cd")).define("Enable Show Item Cd", false);
            enableCombatPrompts = builder.comment("If set to 'False', Disable all combat encounter prompts")
                    .translation(getTranslationKey("combat_prompt")).define("Enable Combat Prompt Trigger Detection", true);
            builder.pop();
        }

        //启用显示boss血条
        public final ConfigValue<Boolean> enableShowBossBars;
        //boss血条显示距离
        public final ConfigValue<Double> bossBarMaxDist;
        //启用模组生物破坏性行为
        public final ConfigValue<Boolean> enableMobGriefing;
        //启用破坏方块掉落对应方块物品
        public final ConfigValue<Boolean> enableItemDropsWhenBreakBlocks;
        //启用显示物品冷却时间
        public final ConfigValue<Boolean> enableShowItemCD;
        //启用战斗提示
        public final ConfigValue<Boolean> enableCombatPrompts;
    }

    //通用属性倍率
    public static class AttributeConfig {
        public AttributeConfig() {
            this(1.0F, 1.0F);
        }

        public AttributeConfig(float healthMultiplier, float attackMultiplier) {
            this.healthMultiplier = COMMON_BUILDER.comment("Set this mob health multiplier")
                    .translation(getTranslationKey("mob_health")).defineInRange("Health Multiplier", healthMultiplier, 0D, Float.MAX_VALUE);
            this.attackMultiplier = COMMON_BUILDER.comment("Set this mob attack multiplier")
                    .translation(getTranslationKey("mob_attack")).defineInRange("Attack Multiplier", attackMultiplier, 0D, Float.MAX_VALUE);
            damage = null;
        }

        public AttributeConfig(float damageAmount) {
            damage = COMMON_BUILDER.comment("Set this entity attack damage")
                    .translation(getTranslationKey("entity_damage")).defineInRange("Attack Damage", damageAmount, 1D, Float.MAX_VALUE);
            attackMultiplier = null;
            healthMultiplier = null;
        }

        public final ConfigValue<Double> healthMultiplier;
        public final ConfigValue<Double> attackMultiplier;
        public final ConfigValue<Double> damage;
    }

    //Boss通用配置
    public static class BossConfig {
        public BossConfig(double damageCap, double damageDist, double damageReductDur) {
            this.damageCap = COMMON_BUILDER.comment("Set this mob damage cap")
                    .translation(getTranslationKey("damage_cap"))
                    .defineInRange("Damage Cap", damageCap, 0D, Float.MAX_VALUE);
            this.canDamageDist = COMMON_BUILDER.comment("Set this mob max effective damage distance")
                    .translation(getTranslationKey("max_damage_dist")).defineInRange("Effective Damage Distance", damageDist, 0D, 64D);
            this.damageReductDur = COMMON_BUILDER.comment("Set this mob's damage reduction duration after being damaged (seconds)")
                    .translation(getTranslationKey("damage_reduction_duration"))
                    .defineInRange("Damage Reduction Duration", damageReductDur, 0D, 5D);
        }

        public final ConfigValue<Double> damageCap;
        public final ConfigValue<Double> canDamageDist;
        public final ConfigValue<Double> damageReductDur;
    }

    //伤害源适应
    public static class DamageSourceAdaptConfig {
        public DamageSourceAdaptConfig(int attenuatedDuration, int fullAdaptDuration, double singleAdaptFactor, double playerSingleAdaptFactor, double maxAdaptFactor) {
            COMMON_BUILDER.push("Damage Adapt V1");
            this.enabled = COMMON_BUILDER.comment("If set to 'False', Disable Damage Adaptation")
                    .translation(getTranslationKey("damage_adapt_enabled")).define("Enabled", true);
            this.debug = COMMON_BUILDER.comment("If set to 'False', Disable print logs")
                    .translation(getTranslationKey("damage_adapt_debug")).define("Enabled Debug Mode", false);
            this.attenuatedDuration = COMMON_BUILDER.comment("Duration for adaptation to decay to zero when not fully adapted (in seconds)")
                    .translation(getTranslationKey("damage_adapt_0")).defineInRange("Decay Duration", attenuatedDuration, 1, 300);
            this.fullAdaptDuration = COMMON_BUILDER.comment("Set the effective duration for a single adaptation damage source (in seconds)")
                    .translation(getTranslationKey("damage_adapt_1")).defineInRange("Adaptation Duration", fullAdaptDuration, 1, 300);
            this.singleAdaptFactor = COMMON_BUILDER.comment("Adaptation increment per hit for mob-caused damage, scaled by damage/maxHealth. Higher = faster buildup")
                    .translation(getTranslationKey("damage_adapt_2")).defineInRange("Mob Damage Adaptation Factor", singleAdaptFactor, 1D, 10D);
            this.playerSingleAdaptFactor = COMMON_BUILDER.comment("Adaptation increment per hit for player-caused damage, scaled by damage/maxHealth. Higher = faster buildup")
                    .translation(getTranslationKey("damage_adapt_2_player")).defineInRange("Player-Caused Damage Adaptation Factor", playerSingleAdaptFactor, 1D, 10D);
            this.maxAdaptFactor = COMMON_BUILDER.comment("Set the maximum damage reduction factor")
                    .translation(getTranslationKey("damage_adapt_3")).defineInRange("Same Damage Max Adaptation Factor", maxAdaptFactor, 0.1D, 1D);
            COMMON_BUILDER.pop();
        }

        public final ConfigValue<Boolean> enabled;
        public final ConfigValue<Boolean> debug;
        public final ConfigValue<Integer> fullAdaptDuration;
        public final ConfigValue<Integer> attenuatedDuration;
        public final ConfigValue<Double> playerSingleAdaptFactor;
        public final ConfigValue<Double> singleAdaptFactor;
        public final ConfigValue<Double> maxAdaptFactor;
    }

    //通用物品配置
    public static class ToolConfig {
        ToolConfig(double d0, double d1) {
            attackSpeedValue = d1;
            attackDamageValue = d0;
            this.attackDamage = COMMON_BUILDER.comment("Set tool attack damage(Restart required)")
                    .translation(getTranslationKey("tool_damage")).defineInRange("Attack Damage", d0, 0D, Float.MAX_VALUE);
            this.attackSpeed = COMMON_BUILDER.comment("Set tool attack speed(Restart required)")
                    .translation(getTranslationKey("tool_speed")).defineInRange("Attack Speed", d1, 0D, Float.MAX_VALUE);
        }

        public final ConfigValue<Double> attackDamage;
        public final ConfigValue<Double> attackSpeed;

        public double attackDamageValue;
        public double attackSpeedValue;
    }

    /* ↓↓↓ 客户端配置 ↓↓↓ */

    public static class Client {
        public Client(final ConfigBuilder builder) {
            tipNotification = new TipNotification(builder);
            enableCameraShake = builder.comment("If set to 'False', Disable camera shake")
                    .translation(getTranslationKey("camera_shake")).define("Enable Camera Shake", true);
            enableCustomBossBars = builder.comment("If set to 'False', Disable custom bosse health bar")
                    .translation(getTranslationKey("custom_boss_bar")).define("Enable Custom Boss Health Bars", true);
            enablePlayBossMusic = builder.comment("If set to 'False', Disable play boss music")
                    .translation(getTranslationKey("play_boss_music")).define("Enable Play Bosses Musics", true);
            enableFallingBlockRender = builder.comment("If set to 'False', Disable falling block rendering")
                    .translation(getTranslationKey("falling_block_render")).define("Enable Falling Block Render", true);
        }

        public final TipNotification tipNotification;
        public final ConfigValue<Boolean> enableCameraShake;
        public final ConfigValue<Boolean> enablePlayBossMusic;
        public final ConfigValue<Boolean> enableFallingBlockRender;
        public final ConfigValue<Boolean> enableCustomBossBars;
    }

    public static class TipNotification {
        public TipNotification(final ConfigBuilder builder) {
            builder.push("Popup Notification");
            enabled = builder.comment("If set to 'False', Disable popup notification")
                    .translation(getTranslationKey("notification_enabled")).define("Enabled", true);
            displayMode = builder
                    .comment("HUD display mode: FADE_IN_OUT (fade in/out), IMMEDIATE (show immediately)")
                    .translation(getTranslationKey("notification_display_mode")).defineEnum("Display Mode", DisplayMode.FADE_IN_OUT);
            displayDuration = builder
                    .comment("HUD display duration in ticks").translation(getTranslationKey("notification_display_duration"))
                    .defineInRange("Display Duration", 120, 20, 1200);
            backgroundOpacity = builder
                    .comment("HUD background opacity").translation(getTranslationKey("notification_background_opacity"))
                    .defineInRange("Background Opacity", 0.25D, 0.0D, 1.0D);
            textMaxWidth = builder
                    .comment("Maximum width for text lines in pixels").translation(getTranslationKey("notification_text_max_width"))
                    .defineInRange("Text Max Width", 300, 100, 400);
            lineSpacing = builder.comment("Spacing between text lines in pixels")
                    .translation(getTranslationKey("notification_line_spacing")).defineInRange("Line Spacing", 2, 0, 10);
            hudPositionY = builder.comment("Vertical position of HUD from top of screen in pixels")
                    .translation(getTranslationKey("notification_hud_position_y")).defineInRange("Hud Position Y", 30, 0, 100);
            builder.pop();
        }

        public final ConfigValue<Boolean> enabled;
        public final ConfigValue<DisplayMode> displayMode;
        public final ConfigValue<Integer> displayDuration;
        public final ConfigValue<Double> backgroundOpacity;
        public final ConfigValue<Integer> textMaxWidth;
        public final ConfigValue<Integer> lineSpacing;
        public final ConfigValue<Integer> hudPositionY;

        public enum DisplayMode {
            FADE_IN_OUT,
            IMMEDIATE
        }
    }

    /**
     * Stand-in for ModConfigSpec.Builder. push()/pop() build a dot-joined path (lower_snake_case,
     * matching the old "path segment" names) that each define* call combines with its own key to make
     * the ConfigValue's unique JSON key; comment()/translation() are recorded/no-op respectively; every
     * define* overload below matches the ModConfigSpec.Builder overload it replaces so call sites did not
     * need to change. Every ConfigValue created through a given builder instance is collected into
     * {@link #values()} so ModConfigHandler's static initializer can load/save the whole tree in one pass.
     */
    public static final class ConfigBuilder {
        private final Deque<String> path = new ArrayDeque<>();
        private final List<ConfigValue<?>> values = new ArrayList<>();
        private String pendingComment;

        public ConfigBuilder push(String category) {
            path.push(slug(category));
            return this;
        }

        public ConfigBuilder pop() {
            path.pop();
            return this;
        }

        public ConfigBuilder comment(String comment) {
            this.pendingComment = comment;
            return this;
        }

        public ConfigBuilder translation(String key) {
            // No config GUI on this branch to feed a translation key to - kept as a no-op so the
            // .comment(...).translation(...).define(...) chains below didn't need rewriting.
            return this;
        }

        public ConfigValue<Boolean> define(String name, boolean defaultValue) {
            return add(new ConfigValue<>(key(name), takeComment(), defaultValue, null, null));
        }

        public ConfigValue<Double> defineInRange(String name, double defaultValue, double min, double max) {
            return add(new ConfigValue<>(key(name), takeComment(), defaultValue, min, max));
        }

        public ConfigValue<Integer> defineInRange(String name, int defaultValue, int min, int max) {
            return add(new ConfigValue<>(key(name), takeComment(), defaultValue, min, max));
        }

        public <E extends Enum<E>> ConfigValue<E> defineEnum(String name, E defaultValue) {
            return add(new ConfigValue<>(key(name), takeComment(), defaultValue, null, null));
        }

        private <T> ConfigValue<T> add(ConfigValue<T> value) {
            values.add(value);
            return value;
        }

        private String takeComment() {
            String c = pendingComment;
            pendingComment = null;
            return c;
        }

        private String key(String name) {
            StringBuilder sb = new StringBuilder();
            List<String> segments = new ArrayList<>(path);
            for (int i = segments.size() - 1; i >= 0; i--) {
                sb.append(segments.get(i)).append('.');
            }
            sb.append(slug(name));
            return sb.toString();
        }

        private static String slug(String s) {
            return s.trim().toLowerCase(Locale.ROOT).replace(' ', '_').replace("&", "and");
        }

        List<ConfigValue<?>> values() {
            return values;
        }
    }

    /**
     * Stand-in for ModConfigSpec's *Value classes (BooleanValue/DoubleValue/IntValue/EnumValue&lt;T&gt;),
     * collapsed into one generic type. {@link #get()} is the only accessor the rest of the mod ever
     * calls, so that's the only method that needed to keep working. Bounds (min/max) are only present for
     * numeric values and are enforced (by clamping) both when a stored JSON value is loaded and, as a
     * safety net, on the compiled-in default.
     */
    @SuppressWarnings("unchecked")
    public static final class ConfigValue<T> {
        private final String path;
        private final String comment;
        private final T defaultValue;
        private final Comparable<T> min;
        private final Comparable<T> max;
        private volatile T value;

        private ConfigValue(String path, String comment, T defaultValue, Comparable<?> min, Comparable<?> max) {
            this.path = path;
            this.comment = comment;
            this.defaultValue = clamp(defaultValue, (Comparable<T>) min, (Comparable<T>) max);
            this.min = (Comparable<T>) min;
            this.max = (Comparable<T>) max;
            this.value = this.defaultValue;
        }

        public T get() {
            return value;
        }

        private static <T> T clamp(T value, Comparable<T> min, Comparable<T> max) {
            if (min != null && min.compareTo(value) > 0) return (T) min;
            if (max != null && max.compareTo(value) < 0) return (T) max;
            return value;
        }

        void readFrom(JsonObject object) {
            JsonElement element = object.get(path);
            if (element == null || !element.isJsonPrimitive()) {
                value = defaultValue;
                return;
            }
            try {
                if (defaultValue instanceof Boolean) {
                    value = (T) Boolean.valueOf(element.getAsBoolean());
                } else if (defaultValue instanceof Integer) {
                    value = clamp((T) Integer.valueOf(element.getAsInt()), min, max);
                } else if (defaultValue instanceof Double) {
                    value = clamp((T) Double.valueOf(element.getAsDouble()), min, max);
                } else if (defaultValue instanceof Enum) {
                    T parsed = (T) Enum.valueOf(((Enum<?>) defaultValue).getDeclaringClass(), element.getAsString());
                    value = parsed;
                } else {
                    value = defaultValue;
                }
            } catch (RuntimeException e) {
                EEEABMobs.LOGGER.warn("Ignoring invalid config value for {} ('{}'), keeping default {}", path, element, defaultValue, e);
                value = defaultValue;
            }
        }

        void writeTo(JsonObject object) {
            if (comment != null) {
                // JSON has no comment syntax; the closest equivalent without a custom parser is a sibling
                // "<key>//comment" string entry, which keeps the file grep-able without the reader needing
                // one. Not load-bearing - readFrom() ignores unknown keys.
                object.addProperty(path + "//comment", comment);
            }
            if (value instanceof Enum) {
                object.addProperty(path, ((Enum<?>) value).name());
            } else if (value instanceof Boolean b) {
                object.addProperty(path, b);
            } else if (value instanceof Number n) {
                object.addProperty(path, n);
            } else {
                object.addProperty(path, String.valueOf(value));
            }
        }
    }
}
