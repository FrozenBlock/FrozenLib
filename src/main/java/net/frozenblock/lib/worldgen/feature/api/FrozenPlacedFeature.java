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

import java.util.ArrayList;
import java.util.List;
import net.frozenblock.lib.FrozenLogUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

public class FrozenPlacedFeature {

	/**
	 * Can be used for setting all bootstrap contexts on 1.19.3
	 */
	public static final List<FrozenPlacedFeature> FEATURES = new ArrayList<>();

	private final ResourceKey<PlacedFeature> key;

	private Holder<ConfiguredFeature<?, ?>> configuredHolder;

	public FrozenPlacedFeature(ResourceLocation key) {
		this.key = ResourceKey.create(Registries.PLACED_FEATURE, key);
		FEATURES.add(this);
	}
	public ResourceKey<PlacedFeature> getKey() {
		return key;
	}

	public Holder<ConfiguredFeature<?, ?>> getConfiguredHolder() {
		assert this.configuredHolder.value() != null: "Trying get null holder from placed feature " + this.getKey().location();
		return this.configuredHolder;
	}

	public FrozenPlacedFeature setConfiguredHolder(Holder<ConfiguredFeature<?, ?>> configuredHolder) {
		this.configuredHolder = configuredHolder;
		return this;
	}

	public Holder<PlacedFeature> getHolder() {
		return FrozenFeatureUtils.BOOTSTRAP_CONTEXT.lookup(Registries.PLACED_FEATURE).getOrThrow(this.getKey());
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public <FC extends FeatureConfiguration> FrozenPlacedFeature makeAndSetHolder(Holder<ConfiguredFeature<?, ?>> configuredHolder, List<PlacementModifier> modifiers) {
		setConfiguredHolder(configuredHolder);

		FrozenLogUtils.log("Registering placed feature " + this.getKey().location(), true);

		assert FrozenFeatureUtils.BOOTSTRAP_CONTEXT != null: "Boostrap context is null when writing FrozenPlacedFeature " + this.getKey().location();
		assert configuredHolder != null: "Configured feature holder for FrozenPlacedFeature " + this.getKey().location() + " null";
		assert modifiers != null: "Placement modifiers for FrozenPlacedFeature " + this.getKey().location() + " null";

		FrozenFeatureUtils.BOOTSTRAP_CONTEXT.register((ResourceKey) this.getKey(), new PlacedFeature(configuredHolder, modifiers));

		return this;
	}

	public <FC extends FeatureConfiguration> FrozenPlacedFeature makeAndSetHolder(Holder<ConfiguredFeature<?, ?>> configuredHolder, PlacementModifier... modifiers) {
		return this.makeAndSetHolder(configuredHolder, List.of(modifiers));
	}
}
