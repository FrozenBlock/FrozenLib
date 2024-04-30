/*
 * Copyright 2023 FrozenBlock
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
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.gravity.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record GravityBelt<T extends GravityFunction>(double minY, double maxY, boolean renderBottom, boolean renderTop,
													 T function) {
	public GravityBelt(double minY, double maxY, T function) {
		this(minY, maxY, false, false, function);
	}

	public boolean affectsPosition(double y) {
		return y >= minY && y < maxY;
	}

	public Vec3 getGravity(@Nullable Entity entity, double y) {
		if (this.affectsPosition(y)) {
            return this.function.get(entity, y, this.minY, this.maxY);
		}
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
		Codec<T> codec = gravityFunction.codec();
		if (codec == null) return null;
		return codec(codec);
	}
}
