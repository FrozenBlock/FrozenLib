package net.frozenblock.lib.block.api.piston;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.entrypoint.api.CommonEventEntrypoint;
import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.EventRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public final class PistonEvents {
	/**
	 * The event that is triggered to determine whether a {@link Block} with a {@link BlockEntity} can be pushed by a Piston.
	 */
	public static final Event<DetermineBlockEntityPushResult> DETERMINE_BLOCK_ENTITY_PUSH_RESULT = EventRegistry.createEnvironmentEvent(
		DetermineBlockEntityPushResult.class,
		(callbacks) -> (state, direction) -> {
			for (var callback : callbacks) {
				final PushResult newResult = callback.determineBlockEntityPushResult(state, direction);
				if (newResult != PushResult.PASS) return newResult;
			}
			return PushResult.PASS;
		}
	);

	/**
	 * The event that is triggered to determine whether a {@link Block} is considered sticky when pushed by a Piston.
	 * <p>
	 * Be sure to use {@link #TRY_STICK_BLOCKS_TOGETHER} to determine when {@link Block}s stick to each other, otherwise this event will not result in the intended behavior.
	 */
	public static final Event<DetermineBlockStickiness> DETERMINE_BLOCK_STICKINESS = EventRegistry.createEnvironmentEvent(
		DetermineBlockStickiness.class,
		(callbacks) -> (state, direction) -> {
			for (var callback : callbacks) {
				final StickyResult newResult = callback.determineBlockStickiness(state, direction);
				if (newResult != StickyResult.PASS) return newResult;
			}
			return StickyResult.PASS;
		}
	);

	/**
	 * The event that is triggered to determine whether a {@link Block} will stick to another {@link Block} when pushed by a Piston.
	 * <p>
	 * Be sure to use {@link #DETERMINE_BLOCK_STICKINESS} to enable stickiness first, otherwise this event will not result in the intended behavior.
	 */
	public static final Event<TryStickBlocksTogether> TRY_STICK_BLOCKS_TOGETHER = EventRegistry.createEnvironmentEvent(
		TryStickBlocksTogether.class,
		(callbacks) -> (previousState, nextState, direction) -> {
			for (var callback : callbacks) {
				final StickTogetherResult newResult = callback.tryStickBlocksTogether(previousState, nextState, direction);
				if (newResult != StickTogetherResult.PASS) return newResult;
			}
			return StickTogetherResult.PASS;
		}
	);

	/**
	 * The event that is triggered when a {@link PistonMovingBlockEntity} sets its held block in the level.
	 */
	public static final Event<OnMovingBlockSet> ON_MOVING_BLOCK_SET = EventRegistry.createEnvironmentEvent(
		OnMovingBlockSet.class,
		(callbacks) -> (level, pos, state, pistonMovingBlockEntity) -> {
			for (var callback : callbacks) callback.onMovingBlockSet(level, pos, state, pistonMovingBlockEntity);
		}
	);

	/**
	 * The event that is triggered when {@code addBlockLine} returns {@code false} from the {@link PistonStructureResolver#resolve()} method.
	 * <p>
	 * Do note that this event will trigger every single time {@code checkIfExtend} in {@link  PistonBaseBlock} is run, which includes neighbor updates.
	 * <p>
	 * If the behavior you wish to run should not run each time this event is triggered, it is recommended to use a {@link Property} in your block to determine if it's already been triggered.
	 * <p>
	 * I do know this sounds confusing. An example of this can be found in the {@code Drill} {@link Block} from {@code Netherier Nether}.
	 */
	public static final Event<OnPushFail> ON_PUSH_FAIL = EventRegistry.createEnvironmentEvent(
		OnPushFail.class,
		(callbacks) -> (level, pos, state, direction) -> {
			for (var callback : callbacks) callback.onPushFail(level, pos, state, direction);
		}
	);

	/**
	 * A functional interface representing a determine Block Entity push result event.
	 */
	@FunctionalInterface
	public interface DetermineBlockEntityPushResult extends CommonEventEntrypoint {
		/**
		 * Determines whether a {@link Block} with a {@link BlockEntity} can be pushed by a Piston.
		 * @param state the {@link BlockState} of the {@link Block}
		 * @param direction the {@link Direction} the Piston is pushing toward, if available
		 * @return the {@link PushResult}.
		 * <p>
		 * If {@code PASS}, skip and continue to the next callback.
		 * <p>
		 * If {@code FAIL}, the Piston will not push the {@link BlockEntity}.
		 * <p>
		 * If {@code SUCCESS}, the Piston will push the {@link BlockEntity}.
		 */
		PushResult determineBlockEntityPushResult(BlockState state, @Nullable Direction direction);
	}

	/**
	 * A functional interface representing a determine Block stickiness event.
	 */
	@FunctionalInterface
	public interface DetermineBlockStickiness extends CommonEventEntrypoint {
		/**
		 * Determines whether a {@link Block} is considered sticky when pushed by a Piston.
		 * <p>
		 * Do note that a {@link Block} being sticky does not mean it will cause all blocks to stick to it. This can be defined using {@link #TRY_STICK_BLOCKS_TOGETHER}.
		 * @param state the {@link BlockState} of the {@link Block}
		 * @param direction the {@link Direction} the Piston is pushing toward
		 * @return the {@link StickyResult}.
		 * <p>
		 * If {@code PASS}, skip and continue to the next callback.
		 * <p>
		 * If {@code FAIL}, the {@link Block} will not be considered sticky.
		 * <p>
		 * If {@code SUCCESS}, the {@link Block} will be considered sticky.
		 */
		StickyResult determineBlockStickiness(BlockState state, Direction direction);
	}

	/**
	 * A functional interface representing a try stick Blocks together event.
	 */
	@FunctionalInterface
	public interface TryStickBlocksTogether extends CommonEventEntrypoint {
		/**
		 * Determines whether a {@link Block} will stick to another {@link Block} when pushed by a Piston.
		 * <p>
		 * Do note that this will not result in the intended behavior if the {@link Block} was not considered sticky. This can be defined using {@link #DETERMINE_BLOCK_STICKINESS}.
		 * @param previousState the {@link BlockState} of the first {@link Block}
		 * @param nextState the {@link BlockState} of the second {@link Block}
		 * @param direction the {@link Direction} from the {@code previousState} to the {@code nextState}
		 * @return the {@link StickTogetherResult}.
		 * <p>
		 * If {@code PASS}, skip and continue to the next callback.
		 * <p>
		 * If {@code FAIL}, the {@link Block}s will not stick together.
		 * <p>
		 * If {@code SUCCESS}, the {@link Block}s will stick together.
		 */
		StickTogetherResult tryStickBlocksTogether(BlockState previousState, BlockState nextState, Direction direction);
	}

	/**
	 * A functional interface representing a Moving Block set event.
	 */
	@FunctionalInterface
	public interface OnMovingBlockSet extends CommonEventEntrypoint {
		/**
		 * Runs when a {@link PistonMovingBlockEntity} sets its held block in the level.
		 * @param level the {@link Level} the block is being set in
		 * @param pos the {@link BlockPos} of the block being set
		 * @param state the {@link BlockState} of the block being set
		 * @param pistonMovingBlockEntity the {@link PistonMovingBlockEntity} containing the block
		 */
		void onMovingBlockSet(Level level, BlockPos pos, BlockState state, PistonMovingBlockEntity pistonMovingBlockEntity);
	}

	/**
	 * A functional interface representing a push fail event.
	 */
	@FunctionalInterface
	public interface OnPushFail extends CommonEventEntrypoint {
		/**
		 * Runs when {@code addBlockLine} returns {@code false} from the {@link PistonStructureResolver#resolve()} method.
		 * @param level the current {@link Level}
		 * @param pos the {@link BlockPos} of the block that failed to push
		 * @param state the {@link BlockState} of the block that failed to push
		 * @param direction the {@link Direction} of the attempted push
		 */
		void onPushFail(Level level, BlockPos pos, BlockState state, Direction direction);
	}

	public enum PushResult {
		/**
		 * Skip the current callback and continue.
		 */
		PASS,
		/**
		 * Do not push the block.
		 */
		FAIL,
		/**
		 * Push the block.
		 */
		SUCCESS
	}

	public enum StickyResult {
		/**
		 * Skip the current callback and continue.
		 */
		PASS,
		/**
		 * Consider the block not sticky.
		 */
		FAIL,
		/**
		 * Consider the block sticky.
		 */
		SUCCESS
	}

	public enum StickTogetherResult {
		/**
		 * Skip the current callback and continue.
		 */
		PASS,
		/**
		 * Do not stick the blocks together.
		 */
		FAIL,
		/**
		 * Stick the blocks together.
		 */
		SUCCESS
	}
}
