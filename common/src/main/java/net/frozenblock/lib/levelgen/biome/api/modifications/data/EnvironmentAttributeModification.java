/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.levelgen.biome.api.modifications.data;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.List;
import net.frozenblock.lib.levelgen.biome.api.modifications.BiomeModificationContext;
import net.frozenblock.lib.levelgen.biome.api.modifications.ModificationPhase;
import net.frozenblock.lib.levelgen.biome.api.modifications.data.selector.BiomeSelector;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ARGB;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.loot.ValidationContext;

public class EnvironmentAttributeModification extends BiomeModification {
	private static final String ENVIRONMENT_ATTRIBUTE_REMOVALS_KEY = "removals";
	private static final String ENVIRONMENT_ATTRIBUTE_ADDITIONS_KEY = "additions";
	public static final MapCodec<EnvironmentAttributeModification> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		SELECTOR_MAP_CODEC.forGetter(EnvironmentAttributeModification::selector),
		MODIFICATION_PHASE_MAP_CODEC.forGetter(EnvironmentAttributeModification::phase),
		EnvironmentAttributes.CODEC
			.listOf()
			.optionalFieldOf(ENVIRONMENT_ATTRIBUTE_REMOVALS_KEY, List.of())
			.forGetter(modification -> modification.removals),
		EnvironmentAttributeMap.CODEC_ONLY_POSITIONAL
			.optionalFieldOf(ENVIRONMENT_ATTRIBUTE_ADDITIONS_KEY, EnvironmentAttributeMap.EMPTY)
			.forGetter(modification -> modification.additions)
	).apply(instance, EnvironmentAttributeModification::new));
	private final List<EnvironmentAttribute<?>> removals;
	private final EnvironmentAttributeMap additions;

	public EnvironmentAttributeModification(
		Holder<BiomeSelector> selector,
		ModificationPhase phase,
		List<EnvironmentAttribute<?>> removals,
		EnvironmentAttributeMap additions
	) {
		super(selector, phase);
		this.removals = removals;
		this.additions = additions;
	}

	public static EnvironmentAttributeModification forRemoval(
		Holder<BiomeSelector> selector,
		ModificationPhase phase,
		EnvironmentAttribute<?>... removals
	) {
		return new EnvironmentAttributeModification(selector, phase, Arrays.stream(removals).toList(), EnvironmentAttributeMap.EMPTY);
	}

	public static EnvironmentAttributeModification forAddition(
		Holder<BiomeSelector> selector,
		ModificationPhase phase,
		EnvironmentAttributeMap additions
	) {
		return new EnvironmentAttributeModification(selector, phase, List.of(), additions);
	}

	public static EnvironmentAttributeModification forReplacement(
		Holder<BiomeSelector> selector,
		ModificationPhase phase,
		List<EnvironmentAttribute<?>> removals,
		EnvironmentAttributeMap additions
	) {
		return new EnvironmentAttributeModification(selector, phase, removals, additions);
	}

	@Override
	public void modify(BiomeModificationContext context) {
		context.getAttributes().addAll(this.additions);
	}

	@Override
	BiomeModificationType<? extends BiomeModification> type() {
		return BiomeModificationTypes.ENVIRONMENT_ATTRIBUTE.get();
	}

	@Override
	public void validate(ValidationContext context) {
		super.validate(context);
		if (this.removals.isEmpty() && this.additions.equals(EnvironmentAttributeMap.EMPTY)) {
			context.reportProblem(new EmptyModificationProblem(true, ENVIRONMENT_ATTRIBUTE_REMOVALS_KEY, ENVIRONMENT_ATTRIBUTE_ADDITIONS_KEY));
		}
	}

	// HELPER METHODS
	public static void registerSkyColor(
		BootstrapContext<BiomeModification> context,
		Identifier name,
		TagKey<Biome> biomes,
		ResourceKey<BiomeSelector> primaryBiomeSelector,
		int skyColor
	) {
		registerAdditions(
			context,
			name,
			biomes,
			primaryBiomeSelector,
			ModificationPhase.REPLACEMENTS,
			EnvironmentAttributeMap.builder().set(EnvironmentAttributes.SKY_COLOR, ARGB.vector3fFromRGB24(skyColor)).build()
		);
	}

	public static void registerFogColor(
		BootstrapContext<BiomeModification> context,
		Identifier name,
		TagKey<Biome> biomes,
		ResourceKey<BiomeSelector> primaryBiomeSelector,
		int fogColor
	) {
		registerAdditions(
			context,
			name,
			biomes,
			primaryBiomeSelector,
			ModificationPhase.REPLACEMENTS,
			EnvironmentAttributeMap.builder().set(EnvironmentAttributes.FOG_COLOR, ARGB.vector3fFromRGB24(fogColor)).build()
		);
	}

	public static void registerWaterFogColor(
		BootstrapContext<BiomeModification> context,
		Identifier name,
		TagKey<Biome> biomes,
		ResourceKey<BiomeSelector> primaryBiomeSelector,
		int waterFogColor
	) {
		registerAdditions(
			context,
			name,
			biomes,
			primaryBiomeSelector,
			ModificationPhase.REPLACEMENTS,
			EnvironmentAttributeMap.builder().set(EnvironmentAttributes.WATER_FOG_COLOR, ARGB.vector3fFromRGB24(waterFogColor)).build()
		);
	}

	public static void registerAdditions(
		BootstrapContext<BiomeModification> context,
		Identifier name,
		TagKey<Biome> biomes,
		ResourceKey<BiomeSelector> primaryBiomeSelector,
		ModificationPhase phase,
		EnvironmentAttributeMap attributes
	) {
		context.register(
			createKey(name),
			new EnvironmentAttributeModification(
				Holder.direct(
					BiomeSelector.allOf(
						HolderSet.direct(
							context.lookup(FrozenLibRegistries.BIOME_SELECTOR).getOrThrow(primaryBiomeSelector),
							Holder.direct(BiomeSelector.inBiomeTag(context, biomes))
						)
					)
				),
				phase,
				List.of(),
				attributes
			)
		);
	}
}
