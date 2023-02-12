package net.frozenblock.lib.block.api.shape;

import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;
import java.util.Map;

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

	public static VoxelShape makePlaneFromDirection(Direction direction, float fromSide) {
		double minX = direction.equals(Direction.EAST) ? 16F - fromSide : 0F;
		double minY = direction.equals(Direction.UP) ? 16F - fromSide : 0F;
		double minZ = direction.equals(Direction.SOUTH) ? 16F - fromSide : 0F;
		double maxX = direction.equals(Direction.WEST) ? 0F + fromSide : 16F;
		double maxY = direction.equals(Direction.DOWN) ? 0F + fromSide : 16F;
		double maxZ = direction.equals(Direction.NORTH) ? 0F + fromSide : 16F;
		return Block.box(minX, minY, minZ, maxX, maxY, maxZ);
	}
}
