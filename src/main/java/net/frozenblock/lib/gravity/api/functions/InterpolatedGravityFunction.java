/*
 * Copyright 2023 The Quilt Project
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.gravity.api.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.gravity.api.GravityBelt;
import net.frozenblock.lib.gravity.api.SerializableGravityFunction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record InterpolatedGravityFunction(
	Vec3 gravity
	//double minLerpGravity,
	//double maxLerpGravity,
	//double minLerpY,
	//double maxLerpY
) implements SerializableGravityFunction<InterpolatedGravityFunction> {

	public static final Codec<InterpolatedGravityFunction> CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
			Vec3.CODEC.fieldOf("gravity").forGetter(InterpolatedGravityFunction::gravity)
			//Codec.DOUBLE.fieldOf("minLerpGravity").forGetter(InterpolatedGravityFunction::minLerpGravity),
			//Codec.DOUBLE.fieldOf("maxLerpGravity").forGetter(InterpolatedGravityFunction::maxLerpY),
			//Codec.DOUBLE.fieldOf("minLerpY").forGetter(InterpolatedGravityFunction::minLerpY),
			//Codec.DOUBLE.fieldOf("maxLerpY").forGetter(InterpolatedGravityFunction::maxLerpY)
		).apply(instance, InterpolatedGravityFunction::new)
	);

	public static final Codec<GravityBelt<InterpolatedGravityFunction>> BELT_CODEC = GravityBelt.codec(CODEC);

	@Override
	public Vec3 get(@Nullable Entity entity, double y, double minY, double maxY) {
		double normalizedY = (y - minY) / (maxY - minY);
		//double normalizedY = (y - minLerpY) / (maxLerpY - minLerpY);

		return gravity.scale(normalizedY);
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
