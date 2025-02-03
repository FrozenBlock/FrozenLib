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

package net.frozenblock.lib.worldgen.surface.impl;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.FrozenLibLogUtils;
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

		biomeRegistry.get(this.biomeTagKey).ifPresent((biomes -> {
			for (Holder<Biome> biomeHolder : biomes) {
				biomeHolder.unwrapKey().ifPresent(biomeList::add);
			}
			this.biomes = biomeList;
		}));
		if (this.biomes != null) {
			this.biomeNameTest = Set.copyOf(this.biomes)::contains;
			FrozenLibLogUtils.log("OPTIMIZED A SOURCE :D", FrozenLibConstants.UNSTABLE_LOGGING);
		} else {
			FrozenLibLogUtils.log("COULDN'T OPTIMIZE A SOURCE :(", FrozenLibConstants.UNSTABLE_LOGGING);
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
		return ((OptimizedBiomeTagConditionSource) o).biomeTagKey;
	}
}
