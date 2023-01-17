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

package net.frozenblock.lib.worldgen.surface.impl;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.SurfaceRules;

public final class BiomeTagConditionSource implements SurfaceRules.ConditionSource {
	public static final KeyDispatchDataCodec<BiomeTagConditionSource> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec((instance) ->
			instance.group(
					TagKey.codec(Registry.BIOME_REGISTRY)
							.fieldOf("biome_tag")
							.forGetter(BiomeTagConditionSource::getBiomeTagKey))
					.apply(instance, BiomeTagConditionSource::new)
			)
	);

	private final TagKey<Biome> biomeTagKey;

	public static BiomeTagConditionSource isBiomeTag(TagKey<Biome> biomeTagKey) {
		return new BiomeTagConditionSource(biomeTagKey);
	}

	BiomeTagConditionSource(TagKey<Biome> biomeTagKey) {
		this.biomeTagKey = biomeTagKey;
	}

	public KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
		return CODEC;
	}

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

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof BiomeTagConditionSource biomeConditionSource) {
			return this.biomeTagKey.equals(biomeConditionSource.biomeTagKey);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return this.biomeTagKey.hashCode();
	}

	public String toString() {
		return "BiomeConditionSource[biomeTagKey=" + this.biomeTagKey + "]";
	}

	private static TagKey<Biome> getBiomeTagKey(Object o) {
		return ((BiomeTagConditionSource)o).biomeTagKey;
	}
}
