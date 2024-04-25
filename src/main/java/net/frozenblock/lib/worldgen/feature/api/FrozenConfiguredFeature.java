/*
 * Copyright 2023 The Quilt Project
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
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.worldgen.feature.api;

import java.util.ArrayList;
import java.util.List;
import net.frozenblock.lib.FrozenLogUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import org.jetbrains.annotations.Nullable;

public class FrozenConfiguredFeature<FC extends FeatureConfiguration, C extends ConfiguredFeature<FC, ?>> {

	/**
	 * Can be used for setting all bootstrap contexts on 1.19.3
	 */
	public static final List<FrozenConfiguredFeature<?, ?>> FEATURES = new ArrayList<>();

	private final ResourceKey<ConfiguredFeature<?, ?>> key;

	public FrozenConfiguredFeature(ResourceLocation key) {
		this.key = ResourceKey.create(Registries.CONFIGURED_FEATURE, key);
		FEATURES.add(this);
	}

	public ResourceKey<ConfiguredFeature<?, ?>> getKey() {
		return key;
	}

	public Holder<ConfiguredFeature<?, ?>> getHolder(@Nullable LevelReader level) {
		if (level == null)
			return FrozenFeatureUtils.BOOTSTRAP_CONTEXT.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(this.getKey());
		return level.registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE).getOrThrow(this.getKey());
	}

	public Holder<ConfiguredFeature<?, ?>> getHolder() {
		return getHolder(null);
	}

	public ConfiguredFeature<?, ?> getConfiguredFeature(LevelReader level) {
		return getHolder(level).value();
	}

	@SuppressWarnings("unchecked")
	public <F extends Feature<FC>> FrozenConfiguredFeature<FC, C> makeAndSetHolder(F feature, FC config) {
		FrozenLogUtils.log("Registering configured feature: " + this.getKey().location(), true);

		assert FrozenFeatureUtils.BOOTSTRAP_CONTEXT != null: "Bootstrap context is null while registering " + this.getKey().location();

		assert feature != null: "Feature is null whilst registering " + this.getKey().location();
		assert config != null: "Feature configuration is null whilst registering " + this.getKey().location();

		FrozenFeatureUtils.BOOTSTRAP_CONTEXT.register((ResourceKey) this.getKey(), new ConfiguredFeature<>(feature, config));
		return this;
	}
}
