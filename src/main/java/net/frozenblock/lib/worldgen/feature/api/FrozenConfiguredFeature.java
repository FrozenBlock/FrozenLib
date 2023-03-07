package net.frozenblock.lib.worldgen.feature.api;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Optional;

public class FrozenConfiguredFeature<FC extends FeatureConfiguration, C extends ConfiguredFeature<FC, ?>> {

	private final ResourceKey<ConfiguredFeature<?, ?>> key;
	private Holder<C> holder;

	public FrozenConfiguredFeature(ResourceLocation key) {
		this.key = ResourceKey.create(Registry.CONFIGURED_FEATURE_REGISTRY, key);
	}

	public ResourceKey<ConfiguredFeature<?, ?>> getKey() {
		return key;
	}

	public Holder<@Nullable C> getHolder() {
		if (this.holder == null) {
			return Holder.direct(null);
		}
		return this.holder;
	}

	public FrozenConfiguredFeature<FC, C> setHolder(Holder<C> holder) {
		this.holder = holder;
		return this;
	}

	@SuppressWarnings("unchecked")
	public <F extends Feature<FC>> FrozenConfiguredFeature<FC, C> makeAndSetHolder(@NotNull F feature, @NotNull FC config) {
		Holder<C> holder = (Holder<C>) FeatureUtils.register(this.getKey().location().toString(), feature, config);
		return this.setHolder(holder);
	}
}
