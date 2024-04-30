/*
 * Copyright 2023 FrozenBlock
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
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.worldgen.feature.api;

import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.qsl.frozenblock.core.registry.api.event.DynamicRegistryManagerSetupContext;

public final class FrozenConfiguredFeatureUtils {
	private FrozenConfiguredFeatureUtils() {
		throw new UnsupportedOperationException("FrozenConfiguredFeatureUtils contains only static declarations.");
	}

	public static ResourceKey<ConfiguredFeature<?, ?>> createKey(String namespace, String path) {
		return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(namespace, path));
	}

	public static Holder<? extends ConfiguredFeature<NoneFeatureConfiguration, ?>> register(DynamicRegistryManagerSetupContext context, DynamicRegistryManagerSetupContext.RegistryMap registries, String namespace, String id, Feature<NoneFeatureConfiguration> feature) {
		return register(context, registries, namespace, id, feature, FeatureConfiguration.NONE);
	}

	public static <FC extends FeatureConfiguration, F extends Feature<FC>, C extends ConfiguredFeature<FC, ?>> Holder.Reference<C> register(DynamicRegistryManagerSetupContext context, DynamicRegistryManagerSetupContext.RegistryMap registries, @NotNull String namespace, @NotNull String id, F feature, @NotNull FC config) {
		var configuredRegistry = registries.get(Registries.CONFIGURED_FEATURE);
		final ConfiguredFeature<FC, ?> configuredFeature = new ConfiguredFeature<>(feature, config);
		Registry.register(configuredRegistry, new ResourceLocation(namespace, id), configuredFeature);
		var featureEntry = getExact(registries, configuredFeature);
		return (Holder.Reference<C>) featureEntry;
	}

	public static void register(
			BootstrapContext<ConfiguredFeature<?, ?>> BootstrapContext, ResourceKey<ConfiguredFeature<?, ?>> registryKey, Feature<NoneFeatureConfiguration> feature
	) {
		FeatureUtils.register(BootstrapContext, registryKey, feature, FeatureConfiguration.NONE);
	}

	public static <FC extends FeatureConfiguration, F extends Feature<FC>> Holder<ConfiguredFeature<?, ?>> register(
			BootstrapContext<ConfiguredFeature<?, ?>> entries, ResourceKey<ConfiguredFeature<?, ?>> registryKey, F feature, FC featureConfiguration
	) {
		return entries.register(registryKey, new ConfiguredFeature<>(feature, featureConfiguration));
	}

	public static <FC extends FeatureConfiguration, F extends Feature<FC>> Holder<ConfiguredFeature<?, ?>> register(
			DynamicRegistryManagerSetupContext entries, ResourceKey<ConfiguredFeature<?, ?>> registryKey, F feature, FC featureConfiguration
	) {
		var registry = entries.getRegistries(Set.of(Registries.CONFIGURED_FEATURE));
		var value = registry.register(Registries.CONFIGURED_FEATURE, registryKey.location(), new ConfiguredFeature<>(feature, featureConfiguration));
		return Holder.direct(value);
	}

	public static <FC extends FeatureConfiguration, V extends T, T extends ConfiguredFeature<FC, ?>> Holder.Reference<ConfiguredFeature<FeatureConfiguration, ?>> getExact(DynamicRegistryManagerSetupContext.RegistryMap registries, V value) {
		var configuredRegistry = registries.get(Registries.CONFIGURED_FEATURE);
		var holder = configuredRegistry.getHolderOrThrow(configuredRegistry.getResourceKey(value).orElseThrow());
		var exactHolder = getExactReference(holder);
		return exactHolder;
	}

	public static <FC extends FeatureConfiguration, F extends Feature<FC>, V extends ConfiguredFeature<FC, ?>> Holder.Reference<V> getExactReference(Holder.Reference<?> reference) {
		return (Holder.Reference<V>) reference;
	}

	public static Holder<ConfiguredFeature<?, ?>> getHolder(ResourceKey<ConfiguredFeature<?, ?>> resourceKey) {
		return VanillaRegistries.createLookup().lookupOrThrow(Registries.CONFIGURED_FEATURE).getOrThrow(resourceKey);
	}
}
