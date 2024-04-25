/*
 * Copyright 2023 The Quilt Project
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
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.block.api;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
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
import org.jetbrains.annotations.NotNull;

/**
 * A block that combines an amethyst cluster-type block with a multiface block.
 */
public abstract class MultifaceClusterBlock extends MultifaceBlock implements SimpleWaterloggedBlock {
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


    public MultifaceClusterBlock(int height, int xzOffset, Properties properties) {
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
	@NotNull
    public BlockState updateShape(BlockState state, @NotNull Direction direction, @NotNull BlockState neighborState, @NotNull LevelAccessor world, @NotNull BlockPos pos, @NotNull BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }

        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
	@NotNull
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return blockState.getFluidState().isEmpty();
    }
}
