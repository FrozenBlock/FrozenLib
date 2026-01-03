/*
 * Copyright (C) 2025 FrozenBlock
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

package net.frozenblock.lib.config.newconfig.entry;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public abstract class EntryType<T> {
	public static final EntryType<Boolean> BOOL = create(Codec.BOOL, ByteBufCodecs.BOOL);
	public static final EntryType<Integer> INT = create(Codec.INT, ByteBufCodecs.INT);
	public static final EntryType<Double> DOUBLE = create(Codec.DOUBLE, ByteBufCodecs.DOUBLE);
	public static final EntryType<Float> FLOAT = create(Codec.FLOAT, ByteBufCodecs.FLOAT);
	public static final EntryType<Long> LONG = create(Codec.LONG, ByteBufCodecs.LONG);
	public static final EntryType<String> STRING = create(Codec.STRING, ByteBufCodecs.STRING_UTF8);

	public static <T> EntryType<T> create(Codec<T> codec, StreamCodec<? extends ByteBuf, T> streamCodec) {
		return new EntryType<>() {
			@Override
			public Codec<T> getCodec() {
				return codec;
			}

			@Override
			public StreamCodec<? extends ByteBuf, T> getStreamCodec() {
				return streamCodec;
			}
		};
	}

	private static final Map<EntryType<?>, EntryType<List<?>>> LIST_TYPES = new Object2ObjectLinkedOpenHashMap<>();

	public EntryType<List<T>> asList() {
		if (LIST_TYPES.containsKey(this)) return (EntryType<List<T>>) (Object) LIST_TYPES.get(this);

		final EntryType<List<T>> listType = new EntryType<>() {
			@Override
			public Codec<List<T>> getCodec() {
				return EntryType.this.getCodec().listOf();
			}

			@Override
			public StreamCodec<? extends ByteBuf, List<T>> getStreamCodec() {
				return EntryType.this.getStreamCodec().apply(ByteBufCodecs.list());
			}
		};
		LIST_TYPES.put(this, (EntryType<List<?>>) (Object) listType);
		return listType;
	}

	public abstract Codec<T> getCodec();
	public abstract StreamCodec<? extends ByteBuf, T> getStreamCodec();
}
