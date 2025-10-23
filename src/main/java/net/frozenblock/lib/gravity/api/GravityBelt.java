/*
 * Copyright (C) 2024-2025 FrozenBlock
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

package net.frozenblock.lib.gravity.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record GravityBelt<T extends GravityFunction>(double minY, double maxY, boolean renderBottom, boolean renderTop, T function) {

	public GravityBelt(double minY, double maxY, T function) {
		this(minY, maxY, false, false, function);
	}

	public boolean affectsPosition(double y) {
		return y >= minY && y < maxY;
	}

	public Vec3 getGravity(@Nullable Entity entity, double y) {
		if (this.affectsPosition(y)) return this.function.get(entity, y, this.minY, this.maxY);
		return GravityAPI.DEFAULT_GRAVITY;
	}

	public static <T extends SerializableGravityFunction<T>> Codec<GravityBelt<T>> codec(Codec<T> gravityFunction) {
		return RecordCodecBuilder.create(instance ->
			instance.group(
				Codec.DOUBLE.fieldOf("minY").forGetter(GravityBelt::minY),
				Codec.DOUBLE.fieldOf("maxY").forGetter(GravityBelt::maxY),
				gravityFunction.fieldOf("gravityFunction").forGetter(GravityBelt::function)
			).apply(instance, GravityBelt::new)
		);
	}

	@Nullable
	public static <T extends SerializableGravityFunction<T>> Codec<GravityBelt<T>> codec(T gravityFunction) {
		final Codec<T> codec = gravityFunction.codec();
		if (codec == null) return null;
		return codec(codec);
	}
}
