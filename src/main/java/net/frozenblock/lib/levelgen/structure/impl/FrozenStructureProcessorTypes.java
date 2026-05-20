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

package net.frozenblock.lib.levelgen.structure.impl;

import com.mojang.serialization.MapCodec;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.levelgen.structure.api.BlockStateRespectingRuleProcessor;
import net.frozenblock.lib.levelgen.structure.api.MarkForPostProcessingProcessor;
import net.frozenblock.lib.levelgen.structure.api.WeightedRuleProcessor;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;

public class FrozenStructureProcessorTypes {

	public static void init() {
		register("block_state_respecting_rule", BlockStateRespectingRuleProcessor.MAP_CODEC);
		register("weighted_rule", WeightedRuleProcessor.MAP_CODEC);
		register("mark_for_post_processing", MarkForPostProcessingProcessor.MAP_CODEC);
	}

	private static void register(String id, MapCodec<? extends StructureProcessor> codec) {
		Registry.register(BuiltInRegistries.STRUCTURE_PROCESSOR, FrozenLibConstants.id(id), codec);
	}
}
