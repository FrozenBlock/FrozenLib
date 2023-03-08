/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

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
