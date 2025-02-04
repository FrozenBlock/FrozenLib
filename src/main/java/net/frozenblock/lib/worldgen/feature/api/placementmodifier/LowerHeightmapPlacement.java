/*
 * Copyright (C) 2024-2025 FrozenBlock
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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class LowerHeightmapPlacement extends PlacementModifier {
	public static final MapCodec<LowerHeightmapPlacement> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		(Heightmap.Types.CODEC.fieldOf("heightmap")).forGetter(modifier -> modifier.heightmap)
	).apply(instance, LowerHeightmapPlacement::new));

	private final Heightmap.Types heightmap;

	private LowerHeightmapPlacement(Heightmap.Types heightmap) {
		this.heightmap = heightmap;
	}

	@Contract("_ -> new")
	public static @NotNull LowerHeightmapPlacement onHeightmap(Heightmap.Types heightmap) {
		return new LowerHeightmapPlacement(heightmap);
	}

	@Override
	public @NotNull Stream<BlockPos> getPositions(@NotNull PlacementContext context, RandomSource random, @NotNull BlockPos pos) {
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
