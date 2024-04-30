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

package net.frozenblock.lib.block.api.shape;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class FrozenShapes {
	private static final VoxelShape UP_PLANE = Block.box(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
	private static final VoxelShape DOWN_PLANE = Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
	private static final VoxelShape WEST_PLANE = Block.box(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
	private static final VoxelShape EAST_PLANE = Block.box(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	private static final VoxelShape NORTH_PLANE = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
	private static final VoxelShape SOUTH_PLANE = Block.box(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);

	public static final Map<Direction, VoxelShape> PLANE_BY_DIRECTION = Util.make(Maps.newEnumMap(Direction.class), shapes -> {
		shapes.put(Direction.NORTH, NORTH_PLANE);
		shapes.put(Direction.EAST, EAST_PLANE);
		shapes.put(Direction.SOUTH, SOUTH_PLANE);
		shapes.put(Direction.WEST, WEST_PLANE);
		shapes.put(Direction.UP, UP_PLANE);
		shapes.put(Direction.DOWN, DOWN_PLANE);
	});

	@NotNull
	public static VoxelShape makePlaneFromDirection(@NotNull Direction direction, float fromSide) {
		double minX = direction.equals(Direction.EAST) ? 16F - fromSide : 0F;
		double minY = direction.equals(Direction.UP) ? 16F - fromSide : 0F;
		double minZ = direction.equals(Direction.SOUTH) ? 16F - fromSide : 0F;
		double maxX = direction.equals(Direction.WEST) ? 0F + fromSide : 16F;
		double maxY = direction.equals(Direction.DOWN) ? 0F + fromSide : 16F;
		double maxZ = direction.equals(Direction.NORTH) ? 0F + fromSide : 16F;
		return Block.box(minX, minY, minZ, maxX, maxY, maxZ);
	}

	public static Optional<Vec3> closestPointTo(BlockPos originalPos, @NotNull VoxelShape blockShape, Vec3 point) {
		if (blockShape.isEmpty()) {
			return Optional.empty();
		}
		double x = originalPos.getX();
		double y = originalPos.getY();
		double z = originalPos.getZ();
		Vec3[] vec3s = new Vec3[1];
		blockShape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
			double d = Mth.clamp(point.x(), minX + x, maxX + x);
			double e = Mth.clamp(point.y(), minY + y, maxY + y);
			double f = Mth.clamp(point.z(), minZ + z, maxZ + z);
			if (vec3s[0] == null || point.distanceToSqr(d, e, f) < point.distanceToSqr(vec3s[0])) {
				vec3s[0] = new Vec3(d, e, f);
			}
		});
		return Optional.of(vec3s[0]);
	}
}
