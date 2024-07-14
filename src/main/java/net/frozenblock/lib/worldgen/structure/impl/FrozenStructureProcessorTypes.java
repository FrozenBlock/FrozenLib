package net.frozenblock.lib.worldgen.structure.impl;

import com.mojang.serialization.MapCodec;
import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.worldgen.structure.api.BlockStateRespectingRuleProcessor;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

public class FrozenStructureProcessorTypes {
	public static final StructureProcessorType<BlockStateRespectingRuleProcessor> BLOCK_STATE_RESPECTING_RULE_PROCESSOR = register(
		"block_state_respecting_rule",
		BlockStateRespectingRuleProcessor.CODEC
	);

	public static void init() {
	}

	private static <P extends StructureProcessor> StructureProcessorType<P> register(String id, MapCodec<P> codec) {
		return Registry.register(BuiltInRegistries.STRUCTURE_PROCESSOR, FrozenSharedConstants.id(id), () -> codec);
	}
}
