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

package net.frozenblock.lib.entity.api.variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.core.registries.codec.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.variant.SpawnPrioritySelectors;

public record VariantSpawnInjection(Identifier registryId, Holder variant, SpawnPrioritySelectors spawnConditions) {
	public static final MapCodec<VariantSpawnInjection> MAP_CODEC = Identifier.CODEC.fieldOf("registry").dispatchMap(
		variantSpawnInjection -> variantSpawnInjection.registryId,
		registryId ->
			RecordCodecBuilder.mapCodec(instance -> instance.group(
				instance.point(registryId),
				RegistryFixedCodec.create(ResourceKey.createRegistryKey(registryId)).fieldOf("variant").forGetter(VariantSpawnInjection::variant),
				SpawnPrioritySelectors.CODEC.fieldOf("spawn_conditions").forGetter(VariantSpawnInjection::spawnConditions)
			).apply(instance, VariantSpawnInjection::new))
	);
	public static final Codec<VariantSpawnInjection> CODEC = MAP_CODEC.codec();

	public VariantSpawnInjection(ResourceKey<? extends Registry<?>> registryKey, Holder<?> variant, SpawnPrioritySelectors spawnConditions) {
		this(registryKey.identifier(), variant, spawnConditions);
	}

	public VariantSpawnInjection(Object registryKey, Object variant, Object spawnConditions) {
		this((Identifier) registryKey, (Holder<?>) variant, (SpawnPrioritySelectors) spawnConditions);
	}

	public boolean matchesVariant(Object variant) {
		return variant.equals(this.variant) || variant.equals(this.variant.value()) || variant.equals(this.variant.unwrap());
	}

	public static ResourceKey<VariantSpawnInjection> createKey(Identifier id) {
		return ResourceKey.create(FrozenLibRegistries.VARIANT_SPAWN_INJECTION, id);
	}

	public static void register(
		BootstrapContext<VariantSpawnInjection> context,
		ResourceKey<VariantSpawnInjection> name,
		VariantSpawnInjection variantSpawnInjection
	) {
		context.register(name, variantSpawnInjection);
	}

	public static void register(
		BootstrapContext<VariantSpawnInjection> context,
		ResourceKey<VariantSpawnInjection> name,
		ResourceKey<? extends Registry<?>> registryKey,
		Holder<?> variant,
		SpawnPrioritySelectors spawnConditions
	) {
		register(context, name, new VariantSpawnInjection(registryKey, variant, spawnConditions));
	}

	public static void register(
		BootstrapContext<VariantSpawnInjection> context,
		Identifier name,
		ResourceKey<? extends Registry<?>> registryKey,
		Holder<?> variant,
		SpawnPrioritySelectors spawnConditions
	) {
		register(context, createKey(name), registryKey, variant, spawnConditions);
	}
}
