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

package net.frozenblock.lib.worldgen.surface.impl;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.jetbrains.annotations.NotNull;

public final class BiomeTagConditionSource implements SurfaceRules.ConditionSource {
	public static final KeyDispatchDataCodec<BiomeTagConditionSource> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec((instance) ->
			instance.group(
					TagKey.codec(Registries.BIOME)
							.fieldOf("biome_tag")
							.forGetter(BiomeTagConditionSource::getBiomeTagKey))
					.apply(instance, BiomeTagConditionSource::new)
			)
	);

	private final TagKey<Biome> biomeTagKey;

	public BiomeTagConditionSource(TagKey<Biome> biomeTagKey) {
		this.biomeTagKey = biomeTagKey;
	}

	@Override
	public KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
		return CODEC;
	}

	@Override
	@NotNull
	public SurfaceRules.Condition apply(@NotNull SurfaceRules.Context context) {
		class BiomeTagCondition extends SurfaceRules.LazyYCondition {
			BiomeTagCondition(SurfaceRules.Context context) {
				super(context);
			}

			protected boolean compute() {
				return this.context.biome.get().is(BiomeTagConditionSource.this.biomeTagKey);
			}
		}

		return new BiomeTagCondition(context);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof BiomeTagConditionSource biomeConditionSource) {
			return this.biomeTagKey.equals(biomeConditionSource.biomeTagKey);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.biomeTagKey.hashCode();
	}

	@Override
	@NotNull
	public String toString() {
		return "BiomeConditionSource[biomeTagKey=" + this.biomeTagKey + "]";
	}

	private static TagKey<Biome> getBiomeTagKey(@NotNull Object o) {
		return ((BiomeTagConditionSource)o).biomeTagKey;
	}
}
