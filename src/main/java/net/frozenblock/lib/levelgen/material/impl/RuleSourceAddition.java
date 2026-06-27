package net.frozenblock.lib.levelgen.material.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.SurfaceRules;

/**
 * Appends a {@link SurfaceRules.RuleSource} to a set of {@link DimensionType}s.
 */
public record RuleSourceAddition(HolderSet<DimensionType> dimensions, boolean hasPreliminarySurface, SurfaceRules.RuleSource ruleSource) {
	public static final Codec<RuleSourceAddition> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
		RegistryCodecs.homogeneousList(Registries.DIMENSION_TYPE).fieldOf("dimensions").forGetter(RuleSourceAddition::dimensions),
		Codec.BOOL.optionalFieldOf("has_preliminary_surface", false).forGetter(RuleSourceAddition::hasPreliminarySurface),
		SurfaceRules.RuleSource.CODEC.fieldOf("rule_source").forGetter(RuleSourceAddition::ruleSource)
	).apply(instance, RuleSourceAddition::new));

	public RuleSourceAddition(HolderSet<DimensionType> dimensions, SurfaceRules.RuleSource ruleSource) {
		this(dimensions, false, ruleSource);
	}

	public boolean matches(Holder<DimensionType> dimension) {
		return this.dimensions.contains(dimension);
	}

	public boolean noPreliminarySurface() {
		return !this.hasPreliminarySurface;
	}
}
