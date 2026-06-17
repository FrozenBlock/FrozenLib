/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.block.api.waterlike;

import java.util.Optional;
import net.frozenblock.lib.block.api.shape.FrozenShapes;
import net.frozenblock.lib.block.impl.waterlike.BubbleColumnDirection;
import net.frozenblock.lib.block.impl.waterlike.WaterLikeType;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ColorRGBA;
import net.minecraft.util.RandomSource;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public interface WaterLikeBlock {
	EnumProperty<BubbleColumnDirection> BUBBLE_COLUMN_DIRECTION = EnumProperty.create("bubble_column_direction", BubbleColumnDirection.class);
	float ENTITY_WITHIN_COLLISION_FROM_SIDE = 0.25F;
	int AMBIENT_WHIRLPOOL_SOUND_CHANCE = 200;

	ResourceKey<WaterLikeType> myWaterLikeType();

	default WaterLikeType myWaterLikeType(RegistryAccess registryAccess) {
		return registryAccess.lookupOrThrow(FrozenLibRegistries.WATER_LIKE_TYPE).getOrThrow(this.myWaterLikeType()).value();
	}

	ColorRGBA waterLikeColor();

	float waterFogDistance();

	ParticleOptions dripParticle();

	int dripParticleChance();

	ParticleOptions bubbleParticle();

	ParticleOptions splashParticle();

	boolean supportsBubbleColumns();

	Optional<ParticleOptions> bubbleColumnUpParticle();

	Optional<ParticleOptions> currentDownParticle();

	static boolean supportsBubbleColumns(BlockState state) {
		return state.getBlock() instanceof WaterLikeBlock waterLikeBlock && waterLikeBlock.supportsBubbleColumns();
	}

	static boolean canBubbleColumnSurvive(LevelReader level, BlockPos pos) {
		final BlockState belowState = level.getBlockState(pos.below());
		return belowState.is(Blocks.BUBBLE_COLUMN)
			|| belowState.is(BlockTags.ENABLES_BUBBLE_COLUMN_DRAG_DOWN)
			|| belowState.is(BlockTags.ENABLES_BUBBLE_COLUMN_PUSH_UP)
			|| hasBubbleColumn(belowState);
	}

	static void updateAsBubbleColumn(LevelAccessor level, BlockPos pos, BlockState belowState) {
		updateAsBubbleColumn(level, pos, level.getBlockState(pos), belowState);
	}

	static void updateAsBubbleColumn(LevelAccessor level, BlockPos pos, BlockState occupyState, BlockState belowState) {
		if (!canOccupyAsBubbleColumn(occupyState)) return;

		level.setBlock(pos, getStateAsBubbleColumn(occupyState, belowState), Block.UPDATE_CLIENTS);
		final BlockPos.MutableBlockPos mutable = pos.mutable().move(Direction.UP);
		BlockState mutableState;
		while (true) {
			mutableState = level.getBlockState(mutable);
			if (canOccupyAsBubbleColumn(mutableState)) {
				if (!level.setBlock(mutable, getStateAsBubbleColumn(mutableState, belowState), Block.UPDATE_CLIENTS)) return;
				mutable.move(Direction.UP);
			} else {
				BubbleColumnBlock.updateColumn(Blocks.BUBBLE_COLUMN, level, mutable, level.getBlockState(mutable.immutable().below()));
				return;
			}
		}
	}

	private static BlockState getStateAsBubbleColumn(BlockState occupyState, BlockState belowState) {
		if (occupyState.getBlock() instanceof WaterLikeBlock waterLikeBlock && waterLikeBlock.supportsBubbleColumns()) {
			if (belowState.is(Blocks.BUBBLE_COLUMN)) return occupyState.setValue(BUBBLE_COLUMN_DIRECTION, BubbleColumnDirection.getFromBubbleColumn(belowState));
			if (belowState.is(BlockTags.ENABLES_BUBBLE_COLUMN_PUSH_UP)) return occupyState.setValue(BUBBLE_COLUMN_DIRECTION, BubbleColumnDirection.UP);
			if (belowState.is(BlockTags.ENABLES_BUBBLE_COLUMN_DRAG_DOWN)) return occupyState.setValue(BUBBLE_COLUMN_DIRECTION, BubbleColumnDirection.DOWN);
		}
		return occupyState.setValue(BUBBLE_COLUMN_DIRECTION, BubbleColumnDirection.NONE);
	}

	static boolean canOccupyAsBubbleColumn(BlockState occupyState) {
		if (!supportsBubbleColumns(occupyState)) return false;
		final FluidState occupyFluid = occupyState.getFluidState();
		return supportsBubbleColumns(occupyState)
			&& occupyFluid.is(FluidTags.BUBBLE_COLUMN_CAN_OCCUPY)
			&& occupyFluid.isSource()
			&& occupyFluid.getAmount() >= FluidState.AMOUNT_FULL;
	}

	default void tryEntityInsideAsBubbleColumn(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
		if (!isPrecise || !this.supportsBubbleColumns()) return;

		final Optional<Direction> dragDirection = getDirectionAsBubbleColumn(state);
		if (dragDirection.isEmpty()) return;

		final BlockState stateAbove = level.getBlockState(pos.above());
		final boolean nothingAbove = stateAbove.getCollisionShape(level, pos).isEmpty() && stateAbove.getFluidState().isEmpty();
		if (nothingAbove) {
			entity.onAboveBubbleColumn(dragDirection.get() == Direction.DOWN, pos);
		} else {
			entity.onInsideBubbleColumn(dragDirection.get() == Direction.DOWN);
		}
	}

	static Optional<Direction> getDirectionAsBubbleColumn(BlockState state) {
		return supportsBubbleColumns(state) && state.hasProperty(BUBBLE_COLUMN_DIRECTION) ? state.getValue(BUBBLE_COLUMN_DIRECTION).direction() : Optional.empty();
	}

	static boolean hasBubbleColumn(BlockState state) {
		return supportsBubbleColumns(state) && state.getValueOrElse(BUBBLE_COLUMN_DIRECTION, BubbleColumnDirection.NONE) != BubbleColumnDirection.NONE;
	}

	static boolean isPushingUpAsBubbleColumn(BlockState state) {
		return supportsBubbleColumns(state) && state.getValueOrElse(BUBBLE_COLUMN_DIRECTION, BubbleColumnDirection.NONE) == BubbleColumnDirection.UP;
	}

	static boolean isDraggingDownAsBubbleColumn(BlockState state) {
		return supportsBubbleColumns(state) && state.getValueOrElse(BUBBLE_COLUMN_DIRECTION, BubbleColumnDirection.NONE) == BubbleColumnDirection.DOWN;
	}

	boolean canEvaporateOnPlace();

	Optional<SoundEvent> evaporateSound();

	default void onPlaceForWaterLike(Block block, BlockState state, Level level, BlockPos pos, BlockState replacingState, boolean movedByPiston) {
		if (this.canEvaporateOnPlace() && level.environmentAttributes().getValue(EnvironmentAttributes.WATER_EVAPORATES, pos)) {
			level.destroyBlock(pos, false);
			level.levelEvent(LevelEvent.PARTICLES_WATER_EVAPORATING, pos, 0);
			level.playSound(
				null,
				pos,
				this.evaporateSound().orElse(SoundEvents.FIRE_EXTINGUISH),
				SoundSource.BLOCKS,
				1F,
				(1F + level.getRandom().nextFloat() * 0.2F) * 0.7F
			);
			return;
		}

		if (canOccupyAsBubbleColumn(state) && canBubbleColumnSurvive(level, pos)) level.scheduleTick(pos, block, BubbleColumnBlock.CHECK_PERIOD);
	}

	Optional<TagKey<EntityType<?>>> entityTypesThatStayWithinMe(BlockState state);

	boolean canWithinEntityTypesExitFromTop(BlockState state);

	default VoxelShape getCollisionShapeForWaterLike(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		VoxelShape shape = Shapes.empty();
		if (!(context instanceof EntityCollisionContext entityCollisionContext)) return shape;
		if (entityCollisionContext.getEntity() == null) return shape;

		final Optional<TagKey<EntityType<?>>> typesThatStayWithin = this.entityTypesThatStayWithinMe(state);
		final Entity entity = entityCollisionContext.getEntity();
		if (entity == null || typesThatStayWithin.isEmpty() || !entity.is(typesThatStayWithin.get()) || entity.isPassenger() || entity.isDescending()) return shape;
		if (entity instanceof Mob mob && mob.isLeashed()) return shape;

		if ((entity.isInWater() || (entity.getInBlockState().is(this.myWaterLikeType(entity.registryAccess()).blocks())))) {
			for (Direction direction : Direction.values()) {
				if ((direction == Direction.UP && this.canWithinEntityTypesExitFromTop(state)) || level.getFluidState(pos.relative(direction)).is(FluidTags.WATER)) continue;
				shape = Shapes.or(shape, FrozenShapes.makePlaneFromDirection(direction, ENTITY_WITHIN_COLLISION_FROM_SIDE));
			}
		}

		return shape;
	}

	default BlockState getPlacementStateForWaterLike(BlockPlaceContext context, BlockState defaultBlockState) {
		final BlockState replacingState = context.getLevel().getBlockState(context.getClickedPos());
		return defaultBlockState.trySetValue(
			BUBBLE_COLUMN_DIRECTION,
			replacingState.is(Blocks.BUBBLE_COLUMN) && this.supportsBubbleColumns()
				? BubbleColumnDirection.getFromBubbleColumn(replacingState)
				: BubbleColumnDirection.NONE
			);
	}

	default void onUpdateShapeForWaterLike(
		Block block,
		BlockState state,
		LevelReader level,
		ScheduledTickAccess ticks,
		BlockPos pos,
		Direction direction,
		BlockPos neighborPos,
		BlockState neighborState,
		RandomSource random
	) {
		ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		updateColumn: {
			if (!this.supportsBubbleColumns()) break updateColumn;
			scheduleColumnTick:{
				if (!hasBubbleColumn(state)) break scheduleColumnTick;
				if (!(!canBubbleColumnSurvive(level, pos) || direction.getAxis().isVertical() && !hasBubbleColumn(neighborState) && canOccupyAsBubbleColumn(neighborState))) break scheduleColumnTick;
				ticks.scheduleTick(pos, block, BubbleColumnBlock.CHECK_PERIOD);
			}
			if (direction == Direction.DOWN && neighborState.is(Blocks.BUBBLE_COLUMN)) ticks.scheduleTick(pos, block, BubbleColumnBlock.CHECK_PERIOD);
		}
	}

	default void neighborChangedForWaterLike(
		Block block,
		BlockState state,
		Level level,
		BlockPos pos,
		Block neighborBlock,
		@Nullable Orientation orientation,
		boolean movedByPiston
	) {
		if (this.supportsBubbleColumns()) level.scheduleTick(pos, block, BubbleColumnBlock.CHECK_PERIOD);
	}

	default void tickForWaterLike(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (!this.supportsBubbleColumns()) return;
		updateAsBubbleColumn(level, pos, state, level.getBlockState(pos.below()));
	}

	default void animateTickForWaterLike(BlockState state, Level level, BlockPos pos, RandomSource random) {
		final Optional<Direction> optionalDragDirection = getDirectionAsBubbleColumn(state);
		if (optionalDragDirection.isEmpty()) return;

		final double x = pos.getX();
		final double y = pos.getY();
		final double z = pos.getZ();

		final Direction dragDirection = optionalDragDirection.get();
		if (dragDirection == Direction.DOWN) {
			if (this.currentDownParticle().isPresent()) {
				level.addAlwaysVisibleParticle(
					this.currentDownParticle().get(),
					x + 0.5D, y + 0.8D, z,
					0D, 0D, 0D
				);
			}
			if (random.nextInt(AMBIENT_WHIRLPOOL_SOUND_CHANCE) == 0) {
				level.playLocalSound(
					x, y, z,
					SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT,
					SoundSource.BLOCKS,
					0.2F + random.nextFloat() * 0.2F,
					0.9F + random.nextFloat() * 0.15F,
					false
				);
			}
		} else if (dragDirection == Direction.UP) {
			if (this.bubbleColumnUpParticle().isPresent()) {
				level.addAlwaysVisibleParticle(
					this.bubbleColumnUpParticle().get(),
					x + 0.5D, y, z + 0.5D,
					0D,
					0.04D,
					0D
				);
				level.addAlwaysVisibleParticle(
					this.bubbleColumnUpParticle().get(),
					x + random.nextDouble(), y + random.nextDouble(), z + random.nextDouble(),
					0D,
					0.04D,
					0D
				);
			}
			if (random.nextInt(AMBIENT_WHIRLPOOL_SOUND_CHANCE) == 0) {
				level.playLocalSound(
					x, y, z,
					SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT,
					SoundSource.BLOCKS,
					0.2F + random.nextFloat() * 0.2F,
					0.9F + random.nextFloat() * 0.15F,
					false
				);
			}
		}
	}

	default FluidState getFluidStateForWaterLike() {
		return Fluids.WATER.getSource(false);
	}
}
