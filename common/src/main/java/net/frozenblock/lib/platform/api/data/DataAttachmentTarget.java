package net.frozenblock.lib.platform.api.data;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.NonExtendable
public interface DataAttachmentTarget {

	@Nullable
	default <T> T frozenLib$getAttached(DataAttachmentType<T> type) {
		return type.get(this);
	}

	default <T> T frozenLib$getAttachedOrDefault(DataAttachmentType<T> type, T fallback) {
		return type.getOrDefault(this, fallback);
	}

	default <T> T frozenLib$getAttachedOrThrow(DataAttachmentType<T> type) {
		return type.getAttachedOrThrow(this);
	}

	default <T> T frozenLib$getAttachedOrCreate(DataAttachmentType<T> type) {
		return type.getAttachedOrCreate(this);
	}

	default <T> T frozenLib$getAttachedOrCreate(DataAttachmentType<T> type, Supplier<T> initializer) {
		return type.getAttachedOrCreate(this, initializer);
	}

	default <T> T frozenLib$getAttachedOrSet(DataAttachmentType<T> type, T defaultValue) {
		return type.getAttachedOrSet(this, defaultValue);
	}

	default <T> T frozenLib$getAttachedOrElse(DataAttachmentType<T> type, Supplier<T> defaultValue) {
		return type.getAttachedOrGet(this, defaultValue);
	}

	default <T> void frozenLib$setAttached(DataAttachmentType<T> type, T value) {
		type.set(this, value);
	}

	default <T> void frozenLib$removeAttached(DataAttachmentType<T> type) {
		type.remove(this);
	}

	default <T> boolean frozenLib$hasAttached(DataAttachmentType<T> type) {
		return type.has(this);
	}

	@Nullable
	default <T> T frozenLib$modifyAttached(DataAttachmentType<T> type, UnaryOperator<T> modifier) {
		return type.modifyAttached(this, modifier);
	}
}
