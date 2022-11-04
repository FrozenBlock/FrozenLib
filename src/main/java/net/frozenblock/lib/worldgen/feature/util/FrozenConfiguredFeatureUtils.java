package net.frozenblock.lib.worldgen.feature.util;

import net.frozenblock.lib.worldgen.feature.FrozenConfiguredFeature;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.qsl.frozenblock.core.registry.api.event.DynamicRegistryManagerSetupContext;

public class FrozenConfiguredFeatureUtils {

	public static ResourceKey<ConfiguredFeature<?, ?>> createKey(String namespace, String path) {
		return ResourceKey.create(Registry.CONFIGURED_FEATURE_REGISTRY, new ResourceLocation(namespace, path));
	}

	public static Holder<? extends ConfiguredFeature<NoneFeatureConfiguration, ?>> register(DynamicRegistryManagerSetupContext context, DynamicRegistryManagerSetupContext.RegistryMap registries, String namespace, String id, Feature<NoneFeatureConfiguration> feature) {
		return register(context, registries, namespace, id, feature, FeatureConfiguration.NONE);
	}

	public static <FC extends FeatureConfiguration, F extends Feature<FC>, C extends ConfiguredFeature<FC, ?>> Holder.Reference<C> register(DynamicRegistryManagerSetupContext context, DynamicRegistryManagerSetupContext.RegistryMap registries, @NotNull String namespace, @NotNull String id, F feature, @NotNull FC config) {
		var configuredRegistry = registries.get(Registry.CONFIGURED_FEATURE_REGISTRY);
		final ConfiguredFeature<FC, ?> configuredFeature = new ConfiguredFeature<>(feature, config);
		Registry.register(configuredRegistry, new ResourceLocation(namespace, id), configuredFeature);
		var featureEntry = getExact(registries, configuredFeature);
		return (Holder.Reference<C>) featureEntry;
	}

	public static void register(
			BootstapContext<ConfiguredFeature<?, ?>> bootstapContext, ResourceKey<ConfiguredFeature<?, ?>> registryKey, Feature<NoneFeatureConfiguration> feature
	) {
		FeatureUtils.register(bootstapContext, registryKey, feature, FeatureConfiguration.NONE);
	}

	public static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(
			BootstapContext<ConfiguredFeature<?, ?>> bootstapContext, ResourceKey<ConfiguredFeature<?, ?>> registryKey, F feature, FC featureConfiguration
	) {
		FeatureUtils.register(bootstapContext, registryKey, feature, featureConfiguration);
	}

	public static <FC extends FeatureConfiguration, V extends T, T extends ConfiguredFeature<FC, ?>> Holder.Reference<ConfiguredFeature<FeatureConfiguration, ?>> getExact(DynamicRegistryManagerSetupContext.RegistryMap registries, V value) {
		var configuredRegistry = registries.get(Registry.CONFIGURED_FEATURE_REGISTRY);
		var holder = configuredRegistry.getHolderOrThrow(configuredRegistry.getResourceKey(value).orElseThrow());
		var exactHolder = getExactReference(holder);
		return exactHolder;
	}

	public static <FC extends FeatureConfiguration, F extends Feature<FC>, V extends ConfiguredFeature<FC, ?>> Holder.Reference<V> getExactReference(Holder.Reference<?> reference) {
		return (Holder.Reference<V>) reference;
	}

	public static FrozenConfiguredFeature feature(String namespace, String path, Feature<? extends FeatureConfiguration> feature, FeatureConfiguration featureConfiguration) {
		return new FrozenConfiguredFeature(createKey(namespace, path), feature, featureConfiguration);
	}
}
