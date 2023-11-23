/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.gravity.api.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.gravity.api.GravityBelt;
import net.frozenblock.lib.gravity.api.SerializableGravityFunction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public record InterpolatedGravityFunction(
	double gravity
	//double minLerpGravity,
	//double maxLerpGravity,
	//double minLerpY,
	//double maxLerpY
) implements SerializableGravityFunction<InterpolatedGravityFunction> {

	public static final Codec<InterpolatedGravityFunction> CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
			Codec.DOUBLE.fieldOf("gravity").forGetter(InterpolatedGravityFunction::gravity)
			//Codec.DOUBLE.fieldOf("minLerpGravity").forGetter(InterpolatedGravityFunction::minLerpGravity),
			//Codec.DOUBLE.fieldOf("maxLerpGravity").forGetter(InterpolatedGravityFunction::maxLerpY),
			//Codec.DOUBLE.fieldOf("minLerpY").forGetter(InterpolatedGravityFunction::minLerpY),
			//Codec.DOUBLE.fieldOf("maxLerpY").forGetter(InterpolatedGravityFunction::maxLerpY)
		).apply(instance, InterpolatedGravityFunction::new)
	);

	public static final Codec<GravityBelt<InterpolatedGravityFunction>> BELT_CODEC = GravityBelt.codec(CODEC);

	@Override
	public double get(@Nullable Entity entity, double y, double minY, double maxY) {
		double normalizedY = (y - minY) / (maxY - minY);
		//double normalizedY = (y - minLerpY) / (maxLerpY - minLerpY);

		return gravity * normalizedY;
		/*if (normalizedY < 0.5) {
			return Mth.clamp(Mth.lerp(normalizedY, minLerpGravity, gravity), minLerpGravity, gravity);
		}
		if (normalizedY < 1.0) return Mth.lerp(normalizedY, gravity, maxLerpGravity);
		return maxLerpGravity;*/
	}

	@Override
	public Codec<InterpolatedGravityFunction> codec() {
		return CODEC;
	}
}
