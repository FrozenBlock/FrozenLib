/*
 * Copyright (C) 2024-2026 FrozenBlock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.levelgen.structure.impl.processor;

import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.levelgen.structure.api.processor.BlockStateRespectingRuleProcessor;
import net.frozenblock.lib.levelgen.structure.api.processor.MarkForPostProcessingProcessor;
import net.frozenblock.lib.levelgen.structure.api.processor.WeightedRuleProcessor;
import net.frozenblock.lib.platform.api.registry.FrozenDeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;

public class FrozenLibStructureProcessorTypes {

	public static void init() {
		var register = FrozenDeferredRegister.create(
			Registries.STRUCTURE_PROCESSOR,
			FrozenLibConstants.MOD_ID
		);

<<<<<<<< HEAD:common/src/main/java/net/frozenblock/lib/levelgen/structure/impl/FrozenLibStructureProcessorTypes.java
		register.register("block_state_respecting_rule", () -> BlockStateRespectingRuleProcessor.MAP_CODEC);
		register.register("weighted_rule", () -> WeightedRuleProcessor.MAP_CODEC);
		register.register("mark_for_post_processing", () -> MarkForPostProcessingProcessor.MAP_CODEC);

		register.register();
========
	private static void register(String name, MapCodec<? extends StructureProcessor> codec) {
		Registry.register(BuiltInRegistries.STRUCTURE_PROCESSOR, FrozenLibConstants.id(name), codec);
>>>>>>>> 2cdac879d (make structure processor additions data-driven):src/main/java/net/frozenblock/lib/levelgen/structure/impl/processor/FrozenLibStructureProcessorTypes.java
	}
}
