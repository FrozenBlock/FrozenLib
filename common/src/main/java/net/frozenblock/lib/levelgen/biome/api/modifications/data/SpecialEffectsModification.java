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
import java.util.Optional;
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
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.storage.loot.ValidationContext;

public class SpecialEffectsModification extends BiomeModification {
	private static final String WATER_COLOR_KEY = "water_color";
	private static final String FOLIAGE_COLOR_KEY = "foliage_color";
	private static final String DRY_FOLIAGE_COLOR_KEY = "dry_foliage_color";
	private static final String GRASS_COLOR_KEY = "grass_color";
	private static final String GRASS_COLOR_MODIFIER_KEY = "grass_color_modifier";
	public static final MapCodec<SpecialEffectsModification> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		SELECTOR_MAP_CODEC.forGetter(SpecialEffectsModification::selector),
		MODIFICATION_PHASE_MAP_CODEC.forGetter(SpecialEffectsModification::phase),
		ExtraCodecs.STRING_RGB_COLOR.optionalFieldOf(WATER_COLOR_KEY).forGetter(modifier -> modifier.waterColor),
		ExtraCodecs.STRING_RGB_COLOR.optionalFieldOf(FOLIAGE_COLOR_KEY).forGetter(modifier -> modifier.foliageColor),
		ExtraCodecs.STRING_RGB_COLOR.optionalFieldOf(DRY_FOLIAGE_COLOR_KEY).forGetter(modifier -> modifier.dryFoliageColor),
		ExtraCodecs.STRING_RGB_COLOR.optionalFieldOf(GRASS_COLOR_KEY).forGetter(modifier -> modifier.grassColor),
		BiomeSpecialEffects.GrassColorModifier.CODEC.optionalFieldOf(GRASS_COLOR_MODIFIER_KEY).forGetter(modifier -> modifier.grassColorModifier)
	).apply(instance, SpecialEffectsModification::new));
	private final Optional<Integer> waterColor;
	private final Optional<Integer> foliageColor;
	private final Optional<Integer> dryFoliageColor;
	private final Optional<Integer> grassColor;
	private final Optional<BiomeSpecialEffects.GrassColorModifier> grassColorModifier;

	public SpecialEffectsModification(
		Holder<BiomeSelector> selector,
		ModificationPhase phase,
		Optional<Integer> waterColor,
		Optional<Integer> foliageColor,
		Optional<Integer> dryFoliageColor,
		Optional<Integer> grassColor,
		Optional<BiomeSpecialEffects.GrassColorModifier> grassColorModifier
	) {
		super(selector, phase);
		this.waterColor = waterColor;
		this.foliageColor = foliageColor;
		this.dryFoliageColor = dryFoliageColor;
		this.grassColor = grassColor;
		this.grassColorModifier = grassColorModifier;
	}

	@Override
	public void modify(BiomeModificationContext context) {
		final BiomeModificationContext.EffectsContext specialEffectsContext = context.getEffects();
		this.waterColor.ifPresent(specialEffectsContext::setWaterColor);
		this.foliageColor.ifPresent(specialEffectsContext::setFoliageColorOverride);
		this.dryFoliageColor.ifPresent(specialEffectsContext::setDryFoliageColorOverride);
		this.grassColor.ifPresent(specialEffectsContext::setGrassColorOverride);
		this.grassColorModifier.ifPresent(specialEffectsContext::setGrassColorModifier);
	}

	@Override
	BiomeModificationType<? extends BiomeModification> type() {
		return BiomeModificationTypes.SPECIAL_EFFECTS.get();
	}

	@Override
	public void validate(ValidationContext context) {
		super.validate(context);
		if (this.waterColor.isEmpty()
			&& this.foliageColor.isEmpty()
			&& this.dryFoliageColor.isEmpty()
			&& this.grassColor.isEmpty()
			&& this.grassColorModifier.isEmpty()
		) {
			context.reportProblem(
				new EmptyModificationProblem(
					true,
					WATER_COLOR_KEY,
					FOLIAGE_COLOR_KEY,
					DRY_FOLIAGE_COLOR_KEY,
					GRASS_COLOR_KEY,
					GRASS_COLOR_MODIFIER_KEY
				)
			);
		}
	}

	// HELPER METHODS
	public static void registerWaterColor(
		BootstrapContext<BiomeModification> context,
		Identifier name,
		TagKey<Biome> biomes,
		ResourceKey<BiomeSelector> primaryBiomeSelector,
		int waterColor
	) {
		register(
			context,
			name,
			biomes,
			primaryBiomeSelector,
			Optional.of(waterColor),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty()
		);
	}

	public static void registerWaterAndWaterFogColor(
		BootstrapContext<BiomeModification> context,
		Identifier nameBase,
		TagKey<Biome> biomes,
		ResourceKey<BiomeSelector> primaryBiomeSelector,
		int waterColor,
		int waterFogColor
	) {
		registerWaterColor(context, nameBase, biomes, primaryBiomeSelector, waterColor);
		EnvironmentAttributeModification.registerWaterFogColor(context, nameBase.withPath(path -> path + "_fog"), biomes, primaryBiomeSelector, waterFogColor);
	}

	public static void registerFoliageColor(
		BootstrapContext<BiomeModification> context,
		Identifier name,
		TagKey<Biome> biomes,
		ResourceKey<BiomeSelector> primaryBiomeSelector,
		int foliageColor
	) {
		register(
			context,
			name,
			biomes,
			primaryBiomeSelector,
			Optional.empty(),
			Optional.of(foliageColor),
			Optional.empty(),
			Optional.empty(),
			Optional.empty()
		);
	}

	public static void registerDryFoliageColor(
		BootstrapContext<BiomeModification> context,
		Identifier name,
		TagKey<Biome> biomes,
		ResourceKey<BiomeSelector> primaryBiomeSelector,
		int dryFoliageColor
	) {
		register(
			context,
			name,
			biomes,
			primaryBiomeSelector,
			Optional.empty(),
			Optional.empty(),
			Optional.of(dryFoliageColor),
			Optional.empty(),
			Optional.empty()
		);
	}

	public static void registerGrassColor(
		BootstrapContext<BiomeModification> context,
		Identifier name,
		TagKey<Biome> biomes,
		ResourceKey<BiomeSelector> primaryBiomeSelector,
		int grassColor
	) {
		register(
			context,
			name,
			biomes,
			primaryBiomeSelector,
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.of(grassColor),
			Optional.empty()
		);
	}

	public static void registerGrassColorModifier(
		BootstrapContext<BiomeModification> context,
		Identifier name,
		TagKey<Biome> biomes,
		ResourceKey<BiomeSelector> primaryBiomeSelector,
		BiomeSpecialEffects.GrassColorModifier grassColorModifier
	) {
		register(
			context,
			name,
			biomes,
			primaryBiomeSelector,
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.of(grassColorModifier)
		);
	}

	public static void register(
		BootstrapContext<BiomeModification> context,
		Identifier name,
		TagKey<Biome> biomes,
		ResourceKey<BiomeSelector> primaryBiomeSelector,
		Optional<Integer> waterColor,
		Optional<Integer> foliageColorOverride,
		Optional<Integer> dryFoliageColorOverride,
		Optional<Integer> grassColorOverride,
		Optional<BiomeSpecialEffects.GrassColorModifier> grassColorModifier
	) {
		context.register(
			createKey(name),
			new SpecialEffectsModification(
				Holder.direct(
					BiomeSelector.allOf(
						HolderSet.direct(
							context.lookup(FrozenLibRegistries.BIOME_SELECTOR).getOrThrow(primaryBiomeSelector),
							Holder.direct(BiomeSelector.inBiomeTag(context, biomes))
						)
					)
				),
				ModificationPhase.REPLACEMENTS,
				waterColor,
				foliageColorOverride,
				dryFoliageColorOverride,
				grassColorOverride,
				grassColorModifier
			)
		);
	}
}
