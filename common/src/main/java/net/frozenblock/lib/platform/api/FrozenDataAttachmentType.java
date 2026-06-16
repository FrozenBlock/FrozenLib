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

package net.frozenblock.lib.platform.api;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.frozenblock.lib.platform.FrozenInitPlatformUtils;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

/**
 * A cross-platform per-object data attachment, analogous to Fabric's {@code AttachmentType}
 * and NeoForge's {@code AttachmentType}.
 *
 * <p>{@code holder} is intentionally typed as {@link Object}: common code cannot reference
 * Fabric's {@code AttachmentTarget} or NeoForge's {@code IAttachmentHolder} marker interfaces,
 * since those only exist on their respective platforms. Pass the actual {@code Entity},
 * {@code Level}, {@code BlockEntity}, etc. instance; each platform implementation casts it
 * internally.
 *
 * <p>Create with {@link #builder(Identifier)}.
 *
 * @param <T> the attached value type
 */
public interface FrozenDataAttachmentType<T> {

	static <T> Builder<T> builder(Identifier id) {
		return new Builder<>(id);
	}

	@Nullable
	T get(Object holder);

	T getOrDefault(Object holder, T fallback);

	void set(Object holder, T value);

	void remove(Object holder);

	boolean has(Object holder);

	final class Builder<T> {
		private final Identifier id;
		private Codec<T> codec;
		private StreamCodec<? super ByteBuf, T> streamCodec;

		private Builder(Identifier id) {
			this.id = id;
		}

		public Builder<T> persistent(Codec<T> codec) {
			this.codec = codec;
			return this;
		}

		public Builder<T> sync(StreamCodec<? super ByteBuf, T> streamCodec) {
			this.streamCodec = streamCodec;
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
		public StreamCodec<? super ByteBuf, T> streamCodec() {
			return this.streamCodec;
		}

		public FrozenDataAttachmentType<T> build() {
			return FrozenInitPlatformUtils.DATA_ATTACHMENT.create(this);
		}
	}
}
