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

package net.frozenblock.lib.block.api;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MultifaceSpreadeableBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import java.util.Map;
import java.util.Objects;

/**
 * A block that combines an amethyst cluster-type block with a multiface spreadable block.
 */
public abstract class MultifaceClusterSpreadableBlock extends MultifaceSpreadeableBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty UP = BlockStateProperties.UP;

    protected final VoxelShape northAabb;
    protected final VoxelShape southAabb;
    protected final VoxelShape eastAabb;
    protected final VoxelShape westAabb;
    protected final VoxelShape upAabb;
    protected final VoxelShape downAabb;

    private final Map<Direction, VoxelShape> shapeByDirection;
    private final ImmutableMap<BlockState, VoxelShape> shapesCache;

    public MultifaceClusterSpreadableBlock(int height, int xzOffset, @NotNull Properties properties) {
        super(properties.pushReaction(PushReaction.DESTROY));
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false));
        this.upAabb = Block.box(xzOffset, 0.0, xzOffset, 16 - xzOffset, height, (16 - xzOffset));
        this.downAabb = Block.box(xzOffset, (16 - height), xzOffset, (16 - xzOffset), 16.0, (16 - xzOffset));
        this.northAabb = Block.box(xzOffset, xzOffset, (16 - height), (16 - xzOffset), (16 - xzOffset), 16.0);
        this.southAabb = Block.box(xzOffset, xzOffset, 0.0, (16 - xzOffset), (16 - xzOffset), height);
        this.eastAabb = Block.box(0.0, xzOffset, xzOffset, height, (16 - xzOffset), (16 - xzOffset));
        this.westAabb = Block.box((16 - height), xzOffset, xzOffset, 16.0, (16 - xzOffset), (16 - xzOffset));
        this.shapeByDirection = Util.make(Maps.newEnumMap(Direction.class), shapes -> {
            shapes.put(Direction.NORTH, this.southAabb);
            shapes.put(Direction.EAST, this.westAabb);
            shapes.put(Direction.SOUTH, this.northAabb);
            shapes.put(Direction.WEST, this.eastAabb);
            shapes.put(Direction.UP, this.downAabb);
            shapes.put(Direction.DOWN, this.upAabb);
        });
        this.shapesCache = this.getShapeForEachState(this::calculateMultifaceShape);
    }

    @Override
	@NotNull
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Objects.requireNonNull(this.shapesCache.get(state));
    }

    public VoxelShape calculateMultifaceShape(BlockState state) {
        VoxelShape voxelShape = Shapes.empty();

        for(Direction direction : DIRECTIONS) {
            if (hasFace(state, direction)) {
                voxelShape = Shapes.or(voxelShape, this.shapeByDirection.get(direction));
            }
        }

        return voxelShape.isEmpty() ? Shapes.block() : voxelShape;
    }

	@Override
	protected @NotNull BlockState updateShape(
		@NotNull BlockState blockState,
		LevelReader levelReader,
		ScheduledTickAccess scheduledTickAccess,
		BlockPos blockPos,
		Direction direction,
		BlockPos neighborPos,
		BlockState neighborState,
		RandomSource randomSource
	) {
		if (blockState.getValue(WATERLOGGED)) {
			scheduledTickAccess.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelReader));
		}
		return super.updateShape(blockState, levelReader, scheduledTickAccess, blockPos, direction, neighborPos, neighborState, randomSource);
	}

    @Override
	@NotNull
    public FluidState getFluidState(@NotNull BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

	@Override
	protected boolean propagatesSkylightDown(@NotNull BlockState blockState) {
		return blockState.getFluidState().isEmpty();
	}
}