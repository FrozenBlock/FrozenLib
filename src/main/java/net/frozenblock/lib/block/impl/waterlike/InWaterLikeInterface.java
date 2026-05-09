package net.frozenblock.lib.block.impl.waterlike;

import net.frozenblock.lib.block.api.waterlike.WaterLikeBlock;
import org.jetbrains.annotations.Nullable;
import java.util.Map;

public interface InWaterLikeInterface {
	default void frozenLib$setInWaterLike(WaterLikeType type, boolean inside) {
		throw new AssertionError();
	}

	default void frozenLib$clearInWaterLikes() {
		throw new AssertionError();
	}

	default boolean frozenLib$wasInWaterLike(WaterLikeType type) {
		throw new AssertionError();
	}

	default Map<WaterLikeType, Boolean> frozenLib$inWaterLikeStatuses() {
		throw new AssertionError();
	}

	default void frozenLib$setTouchingWaterLike(WaterLikeType type, boolean touching) {
		throw new AssertionError();
	}

	default void frozenLib$clearTouchingWaterLikes() {
		throw new AssertionError();
	}

	default boolean frozenLib$wasTouchingWaterLike(WaterLikeType type) {
		throw new AssertionError();
	}

	default Map<WaterLikeType, Boolean> frozenLib$touchingWaterLikeStatuses() {
		throw new AssertionError();
	}

	default boolean frozenLib$isTouchingWaterLikeOrUnderWaterAndWaterLike(WaterLikeType type) {
		throw new AssertionError();
	}

	default void frozenLib$setWaterReplacementParticlesFromBlock(@Nullable WaterLikeBlock waterLike) {
		throw new AssertionError();
	}
}
