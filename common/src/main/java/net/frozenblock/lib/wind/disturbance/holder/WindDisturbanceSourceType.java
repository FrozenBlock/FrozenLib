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

package net.frozenblock.lib.wind.disturbance.holder;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.jetbrains.annotations.Nullable;

public enum WindDisturbanceSourceType implements StringRepresentable {
	ENTITY("entity", 0) {
		@Override
		public boolean validate(@Nullable Object source) {
			if ((!(source instanceof Entity entity))) return false;
			return entity.isAlive();
		}
	},
	BLOCK_ENTITY("block_entity", 1) {
		@Override
		public boolean validate(@Nullable Object source) {
			if ((!(source instanceof BlockEntity blockEntity))) return false;
			return !blockEntity.isRemoved();
		}
	},
	CHUNK("chunk", 2) {
		@Override
		public boolean validate(@Nullable Object source) {
			return source instanceof ChunkAccess;
		}
	},
	LEVEL("level", 3) {
		@Override
		public boolean validate(@Nullable Object source) {
			return source instanceof Level;
		}
	};
	public static final Codec<WindDisturbanceSourceType> CODEC = StringRepresentable.fromEnum(WindDisturbanceSourceType::values);
	public static final StreamCodec<ByteBuf, WindDisturbanceSourceType> STREAM_CODEC = ByteBufCodecs.enumCodec(WindDisturbanceSourceType.class, WindDisturbanceSourceType::id);
	private final String name;
	private final int id;

	WindDisturbanceSourceType(String name, int id) {
		this.name = name;
		this.id = id;
	}

	public static @Nullable WindDisturbanceSourceType typeFor(@Nullable Object source) {
		return Arrays.stream(WindDisturbanceSourceType.values())
			.filter(type -> type.validate(source))
			.findFirst().orElse(null);
	}

	public abstract boolean validate(@Nullable Object source);

	@Override
	public String getSerializedName() {
		return this.name;
	}

	private int id() {
		return this.id;
	}
}
