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
