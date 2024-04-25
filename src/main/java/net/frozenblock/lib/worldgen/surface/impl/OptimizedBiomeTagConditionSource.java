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

package net.frozenblock.lib.worldgen.surface.impl;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.frozenblock.lib.FrozenLogUtils;
import net.frozenblock.lib.FrozenSharedConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class OptimizedBiomeTagConditionSource implements SurfaceRules.ConditionSource {
	public static final KeyDispatchDataCodec<OptimizedBiomeTagConditionSource> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec((instance) ->
			instance.group(
					TagKey.codec(Registries.BIOME)
							.fieldOf("biome_tag")
							.forGetter(OptimizedBiomeTagConditionSource::getBiomeTagKey))
					.apply(instance, OptimizedBiomeTagConditionSource::new)
			)
	);

	public final TagKey<Biome> biomeTagKey;
	@Nullable
	public List<ResourceKey<Biome>> biomes;
	@Nullable
	public Predicate<ResourceKey<Biome>> biomeNameTest;

	public static final List<OptimizedBiomeTagConditionSource> INSTANCES = new ArrayList<>();

	public static void optimizeAll(@NotNull Registry<Biome> biomeRegistry) {
		INSTANCES.forEach(optimizedBiomeTagConditionSource -> optimizedBiomeTagConditionSource.optimize(biomeRegistry));
	}

	public void optimize(@NotNull Registry<Biome> biomeRegistry) {
		this.biomes = null;
		this.biomeNameTest = null;
		ArrayList<ResourceKey<Biome>> biomeList = new ArrayList<>();

		biomeRegistry.getTag(this.biomeTagKey).ifPresent((biomes -> {
			for (Holder<Biome> biomeHolder : biomes) {
				biomeHolder.unwrapKey().ifPresent(biomeList::add);
			}
			this.biomes = biomeList;
		}));
		if (this.biomes != null) {
			this.biomeNameTest = Set.copyOf(this.biomes)::contains;
			FrozenLogUtils.log("OPTIMIZED A SOURCE :D", FrozenSharedConstants.UNSTABLE_LOGGING);
		} else {
			FrozenLogUtils.log("COULDN'T OPTIMIZE A SOURCE :(", FrozenSharedConstants.UNSTABLE_LOGGING);
		}
	}

	public OptimizedBiomeTagConditionSource(TagKey<Biome> biomeTagKey) {
		this.biomeTagKey = biomeTagKey;
		INSTANCES.add(this);
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
				if (OptimizedBiomeTagConditionSource.this.biomeNameTest != null) {
					return this.context.biome.get().is(OptimizedBiomeTagConditionSource.this.biomeNameTest);
				}
				return this.context.biome.get().is(OptimizedBiomeTagConditionSource.this.biomeTagKey);
			}
		}

		return new BiomeTagCondition(context);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof OptimizedBiomeTagConditionSource biomeConditionSource) {
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
		return "BiomeConditionSource[biomeTagKey=" + this.biomeTagKey + ", optimized]";
	}

	private static TagKey<Biome> getBiomeTagKey(@NotNull Object o) {
		return ((OptimizedBiomeTagConditionSource)o).biomeTagKey;
	}
}
