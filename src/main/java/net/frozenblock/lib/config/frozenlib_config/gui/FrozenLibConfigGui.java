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

package net.frozenblock.lib.config.frozenlib_config.gui;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.clothconfig.FrozenClothConfig;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public final class FrozenLibConfigGui {

	private static void setupEntries(@NotNull ConfigCategory category, @NotNull ConfigEntryBuilder entryBuilder) {
		var config = FrozenLibConfig.get(true);
		var modifiedConfig = FrozenLibConfig.getWithSync();
		Config<?> configInstance = FrozenLibConfig.INSTANCE;
		var defaultConfig = FrozenLibConfig.INSTANCE.defaultInstance();
		var dataFixer = config.dataFixer;
		category.setBackground(FrozenSharedConstants.id("config.png"));

		var useWindOnNonFrozenServers = category.addEntry(
			FrozenClothConfig.syncedEntry(
				entryBuilder.startBooleanToggle(text("use_wind_on_non_frozenlib_servers"), modifiedConfig.useWindOnNonFrozenServers)
					.setDefaultValue(defaultConfig.useWindOnNonFrozenServers)
					.setSaveConsumer(newValue -> config.useWindOnNonFrozenServers = newValue)
					.setTooltip(tooltip("use_wind_on_non_frozenlib_servers"))
					.build(),
					config.getClass(),
					"useWindOnNonFrozenServers",
					configInstance
				)
		);

		var saveItemCooldowns = category.addEntry(
			FrozenClothConfig.syncedEntry(
				entryBuilder.startBooleanToggle(text("save_item_cooldowns"), modifiedConfig.saveItemCooldowns)
					.setDefaultValue(defaultConfig.saveItemCooldowns)
					.setSaveConsumer(newValue -> config.saveItemCooldowns = newValue)
					.setTooltip(tooltip("save_item_cooldowns"))
					.build(),
					config.getClass(),
					"saveItemCooldowns",
					configInstance
				)
		);

		var removeExperimentalWarning = category.addEntry(
			FrozenClothConfig.syncedEntry(
				entryBuilder.startBooleanToggle(text("remove_experimental_warning"), modifiedConfig.removeExperimentalWarning)
					.setDefaultValue(defaultConfig.removeExperimentalWarning)
					.setSaveConsumer(newValue -> config.removeExperimentalWarning = newValue)
					.setTooltip(tooltip("remove_experimental_warning"))
					.build(),
					config.getClass(),
					"removeExperimentalWarning",
					configInstance
				)
		);

		var wardenSpawnTrackerCommand = category.addEntry(
			FrozenClothConfig.syncedEntry(
				entryBuilder.startBooleanToggle(text("warden_spawn_tracker_command"), modifiedConfig.wardenSpawnTrackerCommand)
					.setDefaultValue(defaultConfig.wardenSpawnTrackerCommand)
					.setSaveConsumer(newValue -> config.wardenSpawnTrackerCommand = newValue)
					.setTooltip(tooltip("warden_spawn_tracker_command"))
					.build(),
					config.getClass(),
					"wardenSpawnTrackerCommand",
					configInstance
				)
		);

		var disabledDataFixTypes = FrozenClothConfig.syncedEntry(
			entryBuilder.startStrList(text("disabled_datafix_types"), modifiedConfig.dataFixer.disabledDataFixTypes)
				.setDefaultValue(defaultConfig.dataFixer.disabledDataFixTypes)
				.setSaveConsumer(newValue -> dataFixer.disabledDataFixTypes = newValue)
				.setTooltip(tooltip("disabled_datafix_types"))
				.requireRestart()
				.build(),
			dataFixer.getClass(),
			"disabledDataFixTypes",
			configInstance
		);

		var datafixerCategory = FrozenClothConfig.createSubCategory(entryBuilder, category, text("datafixer"),
			false,
			tooltip("datafixer"),
			disabledDataFixTypes
		);
	}

	public static Screen buildScreen(Screen parent) {
		var configBuilder = ConfigBuilder.create().setParentScreen(parent).setTitle(text("component.title"));
		configBuilder.setSavingRunnable(FrozenLibConfig.INSTANCE::save);
		var config = configBuilder.getOrCreateCategory(text("config"));
		ConfigEntryBuilder entryBuilder = configBuilder.entryBuilder();
		setupEntries(config, entryBuilder);
		return configBuilder.build();
	}

	public static Component text(String key) {
		return Component.translatable("option." + FrozenSharedConstants.MOD_ID + "." + key);
	}

	public static Component tooltip(String key) {
		return Component.translatable("tooltip." + FrozenSharedConstants.MOD_ID + "." + key);
	}
}
