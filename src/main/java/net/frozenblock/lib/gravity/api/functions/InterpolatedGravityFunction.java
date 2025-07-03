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

package net.frozenblock.lib.gravity.api.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.gravity.api.GravityBelt;
import net.frozenblock.lib.gravity.api.SerializableGravityFunction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record InterpolatedGravityFunction(Vec3 bottomGravity, Vec3 topGravity) implements SerializableGravityFunction<InterpolatedGravityFunction> {
	public static final Codec<InterpolatedGravityFunction> CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
			Vec3.CODEC.fieldOf("bottom_gravity").forGetter(InterpolatedGravityFunction::bottomGravity),
			Vec3.CODEC.fieldOf("top_gravity").forGetter(InterpolatedGravityFunction::topGravity)
		).apply(instance, InterpolatedGravityFunction::new)
	);

	public static final Codec<GravityBelt<InterpolatedGravityFunction>> BELT_CODEC = GravityBelt.codec(CODEC);

	@Contract("_, _, _, _ -> new")
	@Override
	public @NotNull Vec3 get(@Nullable Entity entity, double y, double minY, double maxY) {
		double normalizedY = (y - minY) / (maxY - minY);
		return new Vec3(
			Mth.lerp(normalizedY, this.bottomGravity().x(), this.topGravity().x()),
			Mth.lerp(normalizedY, this.bottomGravity().y(), this.topGravity().y()),
			Mth.lerp(normalizedY, this.bottomGravity().z(), this.topGravity().z())
		);
	}

	@Override
	public Codec<InterpolatedGravityFunction> codec() {
		return CODEC;
	}
}
