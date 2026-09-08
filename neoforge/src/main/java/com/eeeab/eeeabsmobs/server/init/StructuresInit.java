package com.eeeab.eeeabsmobs.server.init;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.server.world.structure.StructureCoreforgeRuins;
import com.eeeab.eeeabsmobs.server.world.structure.StructureBloodyAltar;
import com.eeeab.eeeabsmobs.server.world.structure.StructureEMCustom;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class StructuresInit {
    private static final DeferredRegister<StructureType<?>> STRUCTURES = DeferredRegister.create(Registries.STRUCTURE_TYPE, EEEABMobs.MOD_ID);
    private static final DeferredRegister<StructurePieceType> PIECE_TYPE = DeferredRegister.create(Registries.STRUCTURE_PIECE, EEEABMobs.MOD_ID);
    public static final DeferredHolder<StructureType<?>, StructureType<StructureEMCustom>> CONFIG_STRUCTURES = STRUCTURES.register("em_config", () -> explicitStructureTypeTyping(StructureEMCustom.CODEC));
    public static final DeferredHolder<StructureType<?>, StructureType<StructureCoreforgeRuins>> GULING_STRUCTURE = STRUCTURES.register("coreforge_ruins", () -> explicitStructureTypeTyping(StructureCoreforgeRuins.CODEC));
    public static final DeferredHolder<StructureType<?>, StructureType<StructureBloodyAltar>> BLOODY_ALTAR_STRUCTURE = STRUCTURES.register("bloody_altar", () -> explicitStructureTypeTyping(StructureBloodyAltar.CODEC));
    public static final DeferredHolder<StructurePieceType, StructurePieceType> GUL = PIECE_TYPE.register("coreforge_ruins", () -> StructureCoreforgeRuins.Piece::new);
    public static final DeferredHolder<StructurePieceType, StructurePieceType> BA = PIECE_TYPE.register("bloody_altar", () -> StructureBloodyAltar.Piece::new);

    /**
     * Originally, I had a double lambda ()->()-> for the RegistryObject line above, but it turns out that
     * some IDEs cannot resolve the typing correctly. This method explicitly states what the return type
     * is so that the IDE can put it into the DeferredRegistry properly.
     */
    private static <T extends Structure> StructureType<T> explicitStructureTypeTyping(MapCodec<T> structureCodec) {
        return () -> structureCodec;
    }

    public static void register(IEventBus iEventBus) {
        STRUCTURES.register(iEventBus);
        PIECE_TYPE.register(iEventBus);
    }
}
