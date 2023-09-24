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

package net.frozenblock.lib.config.frozenlib_config.gui;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.config.clothconfig.FrozenClothConfig;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.frozenblock.lib.config.frozenlib_config.defaults.DefaultFrozenLibConfig;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public final class FrozenLibConfigGui {

	private static void setupEntries(ConfigCategory category, ConfigEntryBuilder entryBuilder) {
		var config = FrozenLibConfig.get(true);
		var dataFixer = config.dataFixer;
		category.setBackground(FrozenMain.id("config.png"));

		var useWindOnNonFrozenServers = category.addEntry(entryBuilder.startBooleanToggle(text("use_wind_on_non_frozenlib_servers"), config.useWindOnNonFrozenServers)
			.setDefaultValue(DefaultFrozenLibConfig.USE_WIND_ON_NON_FROZENLIB_SERVERS)
			.setSaveConsumer(newValue -> config.useWindOnNonFrozenServers = newValue)
			.setTooltip(tooltip("use_wind_on_non_frozenlib_servers"))
			.build()
		);

		var saveItemCooldowns = category.addEntry(entryBuilder.startBooleanToggle(text("save_item_cooldowns"), config.saveItemCooldowns)
			.setDefaultValue(DefaultFrozenLibConfig.SAVE_ITEM_COOLDOWNS)
			.setSaveConsumer(newValue -> config.saveItemCooldowns = newValue)
			.setTooltip(tooltip("save_item_cooldowns"))
			.build()
		);

		var removeExperimentalWarning = category.addEntry(entryBuilder.startBooleanToggle(text("remove_experimental_warning"), config.removeExperimentalWarning)
			.setDefaultValue(DefaultFrozenLibConfig.REMOVE_EXPERIMENTAL_WARNING)
			.setSaveConsumer(newValue -> config.removeExperimentalWarning = newValue)
			.setTooltip(tooltip("remove_experimental_warning"))
			.build()
		);

		var wardenSpawnTrackerCommand = category.addEntry(entryBuilder.startBooleanToggle(text("warden_spawn_tracker_command"), config.wardenSpawnTrackerCommand)
			.setDefaultValue(DefaultFrozenLibConfig.WARDEN_SPAWN_TRACKER_COMMAND)
			.setSaveConsumer(newValue -> config.wardenSpawnTrackerCommand = newValue)
			.setTooltip(tooltip("warden_spawn_tracker_command"))
			.build()
		);


		var disabledDataFixTypes = entryBuilder.startStrList(text("disabled_datafix_types"), dataFixer.disabledDataFixTypes)
			.setDefaultValue(DefaultFrozenLibConfig.DISABLED_DATAFIX_TYPES)
			.setSaveConsumer(newValue -> dataFixer.disabledDataFixTypes = newValue)
			.setTooltip(tooltip("disabled_datafix_types"))
			.requireRestart()
			.build();

		var datafixerCategory = FrozenClothConfig.createSubCategory(entryBuilder, category, text("datafixer"),
			false,
			tooltip("datafixer"),
			disabledDataFixTypes
		);
	}

	public static Screen buildScreen(Screen parent) {
		var configBuilder = ConfigBuilder.create().setParentScreen(parent).setTitle(text("component.title"));
		configBuilder.setSavingRunnable(() -> FrozenLibConfig.getConfigInstance().save());
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
