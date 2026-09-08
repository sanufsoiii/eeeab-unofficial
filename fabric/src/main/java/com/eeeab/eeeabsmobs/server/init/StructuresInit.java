package com.eeeab.eeeabsmobs.server.init;

import com.eeeab.eeeabsmobs.EEEABMobs;
import com.eeeab.eeeabsmobs.server.world.structure.StructureCoreforgeRuins;
import com.eeeab.eeeabsmobs.server.world.structure.StructureBloodyAltar;
import com.eeeab.eeeabsmobs.server.world.structure.StructureEMCustom;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public class StructuresInit {
    private static final FabricDeferredRegister<StructureType<?>> STRUCTURES = FabricDeferredRegister.create(BuiltInRegistries.STRUCTURE_TYPE, EEEABMobs.MOD_ID);
    private static final FabricDeferredRegister<StructurePieceType> PIECE_TYPE = FabricDeferredRegister.create(BuiltInRegistries.STRUCTURE_PIECE, EEEABMobs.MOD_ID);
    public static final FabricDeferredRegister.Entry<StructureType<StructureEMCustom>> CONFIG_STRUCTURES = STRUCTURES.register("em_config", () -> explicitStructureTypeTyping(StructureEMCustom.CODEC));
    public static final FabricDeferredRegister.Entry<StructureType<StructureCoreforgeRuins>> GULING_STRUCTURE = STRUCTURES.register("coreforge_ruins", () -> explicitStructureTypeTyping(StructureCoreforgeRuins.CODEC));
    public static final FabricDeferredRegister.Entry<StructureType<StructureBloodyAltar>> BLOODY_ALTAR_STRUCTURE = STRUCTURES.register("bloody_altar", () -> explicitStructureTypeTyping(StructureBloodyAltar.CODEC));
    public static final FabricDeferredRegister.Entry<StructurePieceType> GUL = PIECE_TYPE.register("coreforge_ruins", () -> StructureCoreforgeRuins.Piece::new);
    public static final FabricDeferredRegister.Entry<StructurePieceType> BA = PIECE_TYPE.register("bloody_altar", () -> StructureBloodyAltar.Piece::new);

    /**
     * Originally, I had a double lambda ()->()-> for the RegistryObject line above, but it turns out that
     * some IDEs cannot resolve the typing correctly. This method explicitly states what the return type
     * is so that the IDE can put it into the DeferredRegistry properly.
     */
    private static <T extends Structure> StructureType<T> explicitStructureTypeTyping(MapCodec<T> structureCodec) {
        return () -> structureCodec;
    }

    public static void register(Object iEventBus) {
        STRUCTURES.register(iEventBus);
        PIECE_TYPE.register(iEventBus);
    }
}
