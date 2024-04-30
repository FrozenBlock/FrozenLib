/*
 * Copyright 2023 FrozenBlock
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.worldgen.feature.api.placementmodifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public class LowerHeightmapPlacement extends PlacementModifier {
	public static final MapCodec<LowerHeightmapPlacement> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			(Heightmap.Types.CODEC.fieldOf("heightmap")).forGetter(modifier -> modifier.heightmap)
	).apply(instance, LowerHeightmapPlacement::new));

	private final Heightmap.Types heightmap;

	private LowerHeightmapPlacement(Heightmap.Types heightmap) {
		this.heightmap = heightmap;
	}

	public static LowerHeightmapPlacement onHeightmap(Heightmap.Types heightmap) {
		return new LowerHeightmapPlacement(heightmap);
	}

	@Override
	public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {
		int x = pos.getX();
		int z = pos.getZ();
		int y = context.getHeight(this.heightmap, x, z) - 1;
		if (y > context.getMinBuildHeight()) {
			return Stream.of(new BlockPos(x, y, z));
		}
		return Stream.of(new BlockPos[0]);
	}

	@Override
	public PlacementModifierType<?> type() {
		return FrozenPlacementModifiers.ACCURATE_HEIGHTMAP;
	}

	public static final PlacementModifier HEIGHTMAP_MOTION_BLOCKING = LowerHeightmapPlacement.onHeightmap(Heightmap.Types.MOTION_BLOCKING);
	public static final PlacementModifier HEIGHTMAP_TOP_SOLID = LowerHeightmapPlacement.onHeightmap(Heightmap.Types.OCEAN_FLOOR_WG);
	public static final PlacementModifier HEIGHTMAP_WORLD_SURFACE = LowerHeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG);
	public static final PlacementModifier HEIGHTMAP_OCEAN_FLOOR = LowerHeightmapPlacement.onHeightmap(Heightmap.Types.OCEAN_FLOOR);
}
