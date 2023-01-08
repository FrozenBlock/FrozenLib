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
