/*
 * Copyright (C) 2025 FrozenBlock
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

package net.frozenblock.lib.block.api.shape;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.Map;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

/**
 * A class that helps with making block shapes.
 */
@UtilityClass
public class FrozenShapes {
	private static final VoxelShape UP_PLANE = Block.box(0D, 15D, 0D, 16D, 16D, 16D);
	private static final VoxelShape DOWN_PLANE = Block.box(0D, 0D, 0D, 16D, 1D, 16D);
	private static final VoxelShape WEST_PLANE = Block.box(0D, 0D, 0D, 1D, 16D, 16D);
	private static final VoxelShape EAST_PLANE = Block.box(15D, 0D, 0D, 16D, 16D, 16D);
	private static final VoxelShape NORTH_PLANE = Block.box(0D, 0D, 0D, 16D, 16D, 1D);
	private static final VoxelShape SOUTH_PLANE = Block.box(0D, 0D, 15D, 16D, 16D, 16D);

	public static final Map<Direction, VoxelShape> PLANE_BY_DIRECTION = Util.make(Maps.newEnumMap(Direction.class), shapes -> {
		shapes.put(Direction.NORTH, NORTH_PLANE);
		shapes.put(Direction.EAST, EAST_PLANE);
		shapes.put(Direction.SOUTH, SOUTH_PLANE);
		shapes.put(Direction.WEST, WEST_PLANE);
		shapes.put(Direction.UP, UP_PLANE);
		shapes.put(Direction.DOWN, DOWN_PLANE);
	});

	/**
	 * Returns a {@link VoxelShape} covering the entire face of the given {@link Direction}, with a custom width.
	 *
	 * @param direction The face to cover.
	 * @param width     The width of the plane.
	 * @return a {@link VoxelShape} covering the entire face of the given {@link Direction}, with a custom width.
	 */
	@NotNull
	public static VoxelShape makePlaneFromDirection(@NotNull Direction direction, float width) {
		double minX = direction.equals(Direction.EAST) ? 16F - width : 0F;
		double minY = direction.equals(Direction.UP) ? 16F - width : 0F;
		double minZ = direction.equals(Direction.SOUTH) ? 16F - width : 0F;
		double maxX = direction.equals(Direction.WEST) ? 0F + width : 16F;
		double maxY = direction.equals(Direction.DOWN) ? 0F + width : 16F;
		double maxZ = direction.equals(Direction.NORTH) ? 0F + width : 16F;
		return Block.box(minX, minY, minZ, maxX, maxY, maxZ);
	}

	/**
	 * Returns an {@link ImmutableMap} of planes, with {@link Direction}s as keys.
	 *
	 * @param width The width of each plane.
	 * @return an {@link ImmutableMap} of planes, with {@link Direction}s as keys.
	 */
	public static @NotNull @Unmodifiable ImmutableMap<Direction, VoxelShape> createPlanesForDirection(float width) {
		Map<Direction, VoxelShape> map = new Object2ObjectLinkedOpenHashMap<>();
		for (Direction direction : Direction.values()) map.put(direction, makePlaneFromDirection(direction, width));
		return Maps.immutableEnumMap(map);
	}

	/**
	 * Returns the closest point of a shape to a given position.
	 *
	 * @param originalPos The position of the block.
	 * @param blockShape  The shape being used.
	 * @param point       The position to calculate from.
	 * @return the closest point of a shape to a given position.
	 */
	public static Optional<Vec3> closestPointTo(BlockPos originalPos, @NotNull VoxelShape blockShape, Vec3 point) {
		if (blockShape.isEmpty()) return Optional.empty();
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
