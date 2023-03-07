package net.frozenblock.lib.worldgen.feature.api;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jetbrains.annotations.Nullable;
import java.util.Optional;

public class FrozenPlacedFeature {

	private final ResourceKey<ConfiguredFeature<?, ?>> configuredKey;
	private final ResourceKey<PlacedFeature> key;

	private Holder<ConfiguredFeature<?, ?>> configuredHolder;
	private Holder<PlacedFeature> holder;

	public FrozenPlacedFeature(ResourceLocation configuredKey, ResourceLocation key) {
		this.configuredKey = ResourceKey.create(Registry.CONFIGURED_FEATURE_REGISTRY, configuredKey);
		this.key = ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, key);
	}

	public ResourceKey<ConfiguredFeature<?, ?>> getConfiguredKey() {
		return configuredKey;
	}

	public ResourceKey<PlacedFeature> getKey() {
		return key;
	}

	public Holder<@Nullable ConfiguredFeature<?, ?>> getConfiguredHolder() {
		if (this.configuredHolder == null)
			return Holder.direct(null);
		return this.configuredHolder;
	}

	public FrozenPlacedFeature setConfiguredHolder(Holder<ConfiguredFeature<?, ?>> configuredHolder) {
		this.configuredHolder = configuredHolder;
		return this;
	}

	public Holder<@Nullable PlacedFeature> getHolder() {
		if (this.holder == null)
			return Holder.direct(null);
		return this.holder;
	}

	public FrozenPlacedFeature setHolder(Holder<PlacedFeature> holder) {
		this.holder = holder;
		return this;
	}
}
