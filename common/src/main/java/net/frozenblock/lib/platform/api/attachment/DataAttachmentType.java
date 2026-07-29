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

package net.frozenblock.lib.platform.api.attachment;

import com.mojang.serialization.Codec;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * A cross-platform per-object data attachment, analogous to Fabric's {@code AttachmentType}
 * and NeoForge's {@code AttachmentType}.
 *
 * <p>Create with {@link #builder(Identifier)}.
 *
 * @param <T> the attached value type
 */
public interface DataAttachmentType<T> {

	static <T> DataAttachmentType<T> create(Identifier id, Consumer<Builder<T>> consumer) {
		Builder<T> builder = new Builder<>(id);
		consumer.accept(builder);
		return builder.build();
	}

	static <T> Builder<T> builder(Identifier id) {
		return new Builder<>(id);
	}

	static <T> Builder<T> builder(Identifier id, Supplier<T> initializer) {
		return new Builder<T>(id).initializer(initializer);
	}

	Identifier identifier();

	@Nullable
	T get(DataAttachmentTarget holder);

	T getOrDefault(DataAttachmentTarget holder, T fallback);

	Optional<T> getOptional(DataAttachmentTarget holder);

	void set(DataAttachmentTarget holder, T value);

	void remove(DataAttachmentTarget holder);

	boolean has(DataAttachmentTarget holder);

	/**
	 * A manual force sync
	 */
	void sync(DataAttachmentTarget holder);

	@Nullable
	Supplier<T> initializer();

	boolean isPersistent();

	boolean isSynced();

	boolean copyOnDeath();

	default T getAttachedOrThrow(DataAttachmentTarget holder) {
		return Objects.requireNonNull(get(holder), "No value attached for " + identifier());
	}

	default T getAttachedOrCreate(DataAttachmentTarget holder) {
		final Supplier<T> init = initializer();
		if (init == null) throw new IllegalArgumentException("getAttachedOrCreate() without supplier requires an initializer on the attachment type");
		return getAttachedOrCreate(holder, init);
	}

	default T getAttachedOrCreate(DataAttachmentTarget holder, Supplier<T> initializer) {
		final T value = get(holder);
		if (value != null) return value;

		final T initialized = Objects.requireNonNull(initializer.get(), "initializer result cannot be null");
		set(holder, initialized);
		return initialized;
	}

	default T getAttachedOrSet(DataAttachmentTarget holder, T defaultValue) {
		Objects.requireNonNull(defaultValue, "default value cannot be null");
		final T value = get(holder);
		if (value != null) return value;

		set(holder, defaultValue);
		return defaultValue;
	}

	default T getAttachedOrElseGet(DataAttachmentTarget holder, Supplier<T> defaultValue) {
		Objects.requireNonNull(defaultValue, "default value supplier cannot be null");
		final T value = get(holder);
		return value != null ? value : defaultValue.get();
	}

	default T getAttachedOrElse(DataAttachmentTarget holder, @Nullable T defaultValue) {
		final T value = get(holder);
		return value != null ? value : defaultValue;
	}

	@Nullable
	default T modifyAttached(DataAttachmentTarget holder, UnaryOperator<T> modifier) {
		T previous = get(holder);
		set(holder, modifier.apply(previous));
		return previous;
	}

	final class Builder<T> {
		private final Identifier id;
		private Codec<T> codec;
		private StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec;
		private DataAttachmentSyncPredicate syncPredicate;
		private Supplier<T> initializer;
		private boolean copyOnDeath;
		@Nullable
		private DataAttachmentType<T> built;

		private Builder(Identifier id) {
			this.id = id;
		}

		public Builder<T> persistent(Codec<T> codec) {
			this.codec = codec;
			return this;
		}

		public Builder<T> syncWith(StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
			return syncWith(streamCodec, DataAttachmentSyncPredicate.all());
		}

		public Builder<T> syncWith(StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec, DataAttachmentSyncPredicate syncPredicate) {
			this.streamCodec = streamCodec;
			this.syncPredicate = syncPredicate;
			return this;
		}

		public Builder<T> initializer(Supplier<T> initializer) {
			this.initializer = initializer;
			return this;
		}

		public Builder<T> copyOnDeath() {
			this.copyOnDeath = true;
			return this;
		}

		public Identifier id() {
			return this.id;
		}

		@Nullable
		public Codec<T> codec() {
			return this.codec;
		}

		@Nullable
		public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
			return this.streamCodec;
		}

		@Nullable
		public DataAttachmentSyncPredicate syncPredicate() {
			return this.syncPredicate;
		}

		@Nullable
		public Supplier<T> initializer() {
			return this.initializer;
		}

		public boolean isCopyOnDeath() {
			return this.copyOnDeath;
		}

		@ApiStatus.Internal
		public DataAttachmentType<T> build() {
			if (this.built == null) this.built = DataAttachmentHelper.create(this);
			return this.built;
		}
	}
}
