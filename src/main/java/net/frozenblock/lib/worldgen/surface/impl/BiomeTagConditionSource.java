/*
 * Copyright (C) 2024-2026 FrozenBlock
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
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.SurfaceRules;

/**
 * A {@link SurfaceRules.ConditionSource} that uses a tag to control which biomes it generates in.
 */
public final class BiomeTagConditionSource implements SurfaceRules.ConditionSource {
	public static final KeyDispatchDataCodec<BiomeTagConditionSource> CODEC = KeyDispatchDataCodec.of(
		RecordCodecBuilder.mapCodec(instance ->
			instance.group(
				TagKey.codec(Registries.BIOME).fieldOf("biome_tag").forGetter(BiomeTagConditionSource::getBiomeTagKey)
			).apply(instance, BiomeTagConditionSource::new)
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
	public SurfaceRules.Condition apply(SurfaceRules.Context context) {
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
		if (this == object) return true;
		if (object instanceof BiomeTagConditionSource biomeConditionSource) return this.biomeTagKey.equals(biomeConditionSource.biomeTagKey);
		return false;
	}

	@Override
	public int hashCode() {
		return this.biomeTagKey.hashCode();
	}

	@Override
	public String toString() {
		return "BiomeConditionSource[biomeTagKey=" + this.biomeTagKey + "]";
	}

	private static TagKey<Biome> getBiomeTagKey(Object o) {
		return ((BiomeTagConditionSource) o).biomeTagKey;
	}
}
