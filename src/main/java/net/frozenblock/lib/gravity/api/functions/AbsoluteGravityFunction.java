/*
 * Copyright 2023-2024 FrozenBlock
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record AbsoluteGravityFunction(Vec3 gravity) implements SerializableGravityFunction<AbsoluteGravityFunction> {

	public static final Codec<AbsoluteGravityFunction> CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
			Vec3.CODEC.fieldOf("gravity").forGetter(AbsoluteGravityFunction::gravity)
		).apply(instance, AbsoluteGravityFunction::new)
	);

	public static final Codec<GravityBelt<AbsoluteGravityFunction>> BELT_CODEC = GravityBelt.codec(CODEC);

	@Override
	public Vec3 get(@Nullable Entity entity, double y, double minY, double maxY) {
		return gravity();
	}

	@Override
	public Codec<AbsoluteGravityFunction> codec() {
		return CODEC;
	}
}
