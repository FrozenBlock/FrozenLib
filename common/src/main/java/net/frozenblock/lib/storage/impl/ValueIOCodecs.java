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

package net.frozenblock.lib.storage.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public interface ValueIOCodecs {

	Codec<long[]> LONG_ARRAY = Codec.LONG_STREAM.xmap(LongStream::toArray, LongStream::of);

	Codec<byte[]> BYTE_ARRAY = Codec.BYTE_BUFFER.xmap(buffer -> {
		if (buffer.hasArray()) return buffer.array();
		byte[] bytes = new byte[buffer.capacity()];
		buffer.get(bytes);
		return bytes;
	}, ByteBuffer::wrap);

	MapCodec<Collection<String>> KEYS_EXTRACT = new MapCodec<>() {
		@Override
		public <T> DataResult<Collection<String>> decode(DynamicOps<T> ops, MapLike<T> input) {
			return DataResult.success(input.entries().map(entry -> ops.getStringValue(entry.getFirst()).getOrThrow()).toList());
		}

		@Override
		public <T> RecordBuilder<T> encode(Collection<String> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
			return prefix;
		}

		@Override
		public <T> Stream<T> keys(DynamicOps<T> ops) {
			return Stream.empty();
		}
	};

	static MapCodec<Boolean> contains(String key) {
		return new MapCodec<>() {
			@Override
			public <T> DataResult<Boolean> decode(DynamicOps<T> ops, MapLike<T> input) {
				return DataResult.success(input.get(key) != null);
			}

			@Override
			public <T> RecordBuilder<T> encode(Boolean input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
				return prefix;
			}

			@Override
			public <T> Stream<T> keys(DynamicOps<T> ops) {
				return Stream.empty();
			}
		};
	}
}
