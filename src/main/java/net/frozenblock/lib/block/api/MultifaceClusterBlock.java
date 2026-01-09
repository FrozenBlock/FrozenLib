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

package net.frozenblock.lib.block.api;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A block that combines an amethyst cluster-type block with a multiface block.
 */
public abstract class MultifaceClusterBlock extends MultifaceBlock implements SimpleWaterloggedBlock {
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final BooleanProperty UP = BlockStateProperties.UP;

	protected final VoxelShape northShape;
	protected final VoxelShape southShape;
	protected final VoxelShape eastShape;
	protected final VoxelShape westShape;
	protected final VoxelShape upShape;
	protected final VoxelShape downShape;
    private final Map<Direction, VoxelShape> shapeByDirection;
    private final Function<BlockState, VoxelShape> shapesCache;

    public MultifaceClusterBlock(int height, int xzOffset, Properties properties) {
        super(properties.pushReaction(PushReaction.DESTROY));
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false));
        this.upShape = Block.box(xzOffset, 0D, xzOffset, 16D - xzOffset, height, (16D - xzOffset));
        this.downShape = Block.box(xzOffset, (16D - height), xzOffset, (16D - xzOffset), 16D, (16D - xzOffset));
        this.northShape = Block.box(xzOffset, xzOffset, (16D - height), (16D - xzOffset), (16D - xzOffset), 16D);
        this.southShape = Block.box(xzOffset, xzOffset, 0D, (16D - xzOffset), (16D - xzOffset), height);
        this.eastShape = Block.box(0D, xzOffset, xzOffset, height, (16D - xzOffset), (16D - xzOffset));
        this.westShape = Block.box((16D - height), xzOffset, xzOffset, 16D, (16D - xzOffset), (16D - xzOffset));
        this.shapeByDirection = Util.make(Maps.newEnumMap(Direction.class), shapes -> {
            shapes.put(Direction.NORTH, this.southShape);
            shapes.put(Direction.EAST, this.westShape);
            shapes.put(Direction.SOUTH, this.northShape);
            shapes.put(Direction.WEST, this.eastShape);
            shapes.put(Direction.UP, this.downShape);
            shapes.put(Direction.DOWN, this.upShape);
        });
        this.shapesCache = this.getShapeForEachState(this::calculateMultifaceShape);
    }

	@Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Objects.requireNonNull(this.shapesCache.apply(state));
    }

	public VoxelShape calculateMultifaceShape(BlockState state) {
		VoxelShape shape = Shapes.empty();

		for (Direction direction : DIRECTIONS) {
			if (hasFace(state, direction)) shape = Shapes.or(shape, this.shapeByDirection.get(direction));
		}

		return shape.isEmpty() ? Shapes.block() : shape;
	}

	@Override
	protected BlockState updateShape(
		BlockState state,
		LevelReader level,
		ScheduledTickAccess tickAccess,
		BlockPos pos,
		Direction direction,
		BlockPos neighborPos,
		BlockState neighborState,
		RandomSource random
	) {
		if (state.getValue(WATERLOGGED)) tickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		return super.updateShape(state, level, tickAccess, pos, direction, neighborPos, neighborState, random);
	}

	@Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(WATERLOGGED);
	}

	@Override
	protected boolean propagatesSkylightDown(BlockState state) {
		return state.getFluidState().isEmpty();
	}
}
