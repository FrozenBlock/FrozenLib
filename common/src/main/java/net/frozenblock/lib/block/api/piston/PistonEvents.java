package net.frozenblock.lib.block.api.piston;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.entrypoint.api.CommonEventEntrypoint;
import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.EventRegistry;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
