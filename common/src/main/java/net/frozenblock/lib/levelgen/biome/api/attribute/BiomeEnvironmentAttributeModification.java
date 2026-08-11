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

package net.frozenblock.lib.levelgen.biome.api.attribute;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.config.v2.entry.predicates.ConfigPredicate;
import net.frozenblock.lib.levelgen.biome.api.BiomeSelectors;
import net.frozenblock.lib.levelgen.biome.api.modifications.BiomeModifications;
import net.frozenblock.lib.levelgen.biome.api.modifications.ModificationPhase;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.ApiStatus;

/**
 * @param biomes The {@link Biome}s (in {@link HolderSet} form) to replace the {@link EnvironmentAttribute}s of.
 * @param attributes The {@link EnvironmentAttributeMap} to merge with the {@link Biome}'s {@link EnvironmentAttributeMap}.
 * @param enabledWhen The {@link ConfigPredicate} to test. This instance will be ignored if it returns false.
 */
public record BiomeEnvironmentAttributeModification(HolderSet<Biome> biomes, EnvironmentAttributeMap attributes, ConfigPredicate enabledWhen) {
	public static final Codec<BiomeEnvironmentAttributeModification> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
		RegistryCodecs.homogeneousList(Registries.BIOME).fieldOf("biomes").forGetter(BiomeEnvironmentAttributeModification::biomes),
		EnvironmentAttributeMap.CODEC_ONLY_POSITIONAL.fieldOf("attributes").forGetter(BiomeEnvironmentAttributeModification::attributes),
		ConfigPredicate.CODEC.fieldOf("enabled_when").forGetter(BiomeEnvironmentAttributeModification::enabledWhen)
	).apply(instance, BiomeEnvironmentAttributeModification::new));

	@ApiStatus.Internal
	public static void init() {
		BiomeModifications.create(FrozenLibConstants.id("biome_environment_attribute_modifications")).add(
			ModificationPhase.REPLACEMENTS,
			BiomeSelectors.all(),
			(registryAccess, selectionContext, modificationContext) -> {
				registryAccess.lookupOrThrow(FrozenLibRegistries.BIOME_ENVIRONMENT_ATTRIBUTE_MODIFICATION).forEach(modification -> {
					if (!modification.enabledWhen.test()) return;
					if (!modification.biomes.contains(selectionContext.getBiomeHolder())) return;
					if (modification.attributes.equals(EnvironmentAttributeMap.EMPTY)) return;

					modificationContext.getAttributes().addAll(modification.attributes);
				});
			}
		);
	}

	public static ResourceKey<BiomeEnvironmentAttributeModification> createKey(Identifier id) {
		return ResourceKey.create(FrozenLibRegistries.BIOME_ENVIRONMENT_ATTRIBUTE_MODIFICATION, id);
	}

	public static void register(
		BootstrapContext<BiomeEnvironmentAttributeModification> context,
		ResourceKey<BiomeEnvironmentAttributeModification> name,
		HolderSet<Biome> biomes,
		EnvironmentAttributeMap attributes,
		ConfigPredicate replaceWhen
	) {
		context.register(name, new BiomeEnvironmentAttributeModification(biomes, attributes, replaceWhen));
	}

	public static void register(
		BootstrapContext<BiomeEnvironmentAttributeModification> context,
		ResourceKey<BiomeEnvironmentAttributeModification> name,
		TagKey<Biome> biomes,
		EnvironmentAttributeMap attributes,
		ConfigPredicate replaceWhen
	) {
		register(context, name, context.lookup(Registries.BIOME).getOrThrow(biomes), attributes, replaceWhen);
	}

	public static void register(
		BootstrapContext<BiomeEnvironmentAttributeModification> context,
		Identifier id,
		TagKey<Biome> biomes,
		EnvironmentAttributeMap attributes,
		ConfigPredicate replaceWhen
	) {
		register(context, createKey(id), biomes, attributes, replaceWhen);
	}

	public static void register(
		BootstrapContext<BiomeEnvironmentAttributeModification> context,
		ResourceKey<BiomeEnvironmentAttributeModification> name,
		ResourceKey<Biome> biome,
		EnvironmentAttributeMap attributes,
		ConfigPredicate replaceWhen
	) {
		register(context, name, HolderSet.direct(context.lookup(Registries.BIOME).getOrThrow(biome)), attributes, replaceWhen);
	}

	public static void register(
		BootstrapContext<BiomeEnvironmentAttributeModification> context,
		Identifier id,
		ResourceKey<Biome> biome,
		EnvironmentAttributeMap attributes,
		ConfigPredicate replaceWhen
	) {
		register(context, createKey(id), biome, attributes, replaceWhen);
	}
}
