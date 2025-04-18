/*
 * Copyright (C) 2024-2025 FrozenBlock
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

package net.frozenblock.lib.config.frozenlib_config.gui;

import java.util.List;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.cape.api.CapeUtil;
import net.frozenblock.lib.cape.client.api.ClientCapeUtil;
import net.frozenblock.lib.cape.impl.Cape;
import net.frozenblock.lib.cape.impl.networking.CapeCustomizePacket;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.clothconfig.FrozenClothConfig;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public final class FrozenLibConfigGui {

	private static void setupEntries(@NotNull ConfigCategory category, @NotNull ConfigEntryBuilder entryBuilder) {
		var config = FrozenLibConfig.get(true);
		var modifiedConfig = FrozenLibConfig.getWithSync();
		Config<?> configInstance = FrozenLibConfig.INSTANCE;
		var defaultConfig = FrozenLibConfig.INSTANCE.defaultInstance();
		var dataFixer = config.dataFixer;

		if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
			var isDebug = category.addEntry(
				FrozenClothConfig.syncedEntry(
					entryBuilder.startBooleanToggle(text("is_debug"), modifiedConfig.isDebug)
						.setDefaultValue(defaultConfig.isDebug)
						.setSaveConsumer(newValue -> config.isDebug = newValue)
						.setTooltip(tooltip("is_debug"))
						.build(),
					config.getClass(),
					"isDebug",
					configInstance
				)
			);
		}

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

		var fileTransferServer = category.addEntry(
			FrozenClothConfig.syncedEntry(
				entryBuilder.startBooleanToggle(text("file_transfer_server"), modifiedConfig.fileTransferServer)
					.setDefaultValue(defaultConfig.fileTransferServer)
					.setSaveConsumer(newValue -> config.fileTransferServer = newValue)
					.setTooltip(tooltip("file_transfer_server"))
					.build(),
				config.getClass(),
				"fileTransferServer",
				configInstance
			)
		);

		var fileTransferClient = category.addEntry(
			FrozenClothConfig.syncedEntry(
				entryBuilder.startBooleanToggle(text("file_transfer_client"), modifiedConfig.fileTransferClient)
					.setDefaultValue(defaultConfig.fileTransferClient)
					.setSaveConsumer(newValue -> config.fileTransferClient = newValue)
					.setTooltip(tooltip("file_transfer_client"))
					.build(),
				config.getClass(),
				"fileTransferClient",
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

		List<String> usableCapes = ClientCapeUtil.getUsableCapes(true).stream().map(cape -> cape.registryId().toString()).toList();
		if (usableCapes.size() > 1) {
			var capeEntry = category.addEntry(
				entryBuilder.startSelector(text("cape"), usableCapes.toArray(), modifiedConfig.cape)
					.setDefaultValue(defaultConfig.cape)
					.setNameProvider(o -> {
						ResourceLocation capeId = ResourceLocation.parse(((String) o));
						return CapeUtil.getCape(capeId).map(Cape::capeName).orElse(Component.translatable("cape.frozenlib.invalid"));
					})
					.setSaveConsumer(newValue -> {
						ResourceLocation capeId = ResourceLocation.parse((String) newValue);
						config.cape = (String) newValue;
						if (Minecraft.getInstance().getConnection() != null) {
							ClientPlayNetworking.send(CapeCustomizePacket.createPacket(Minecraft.getInstance().getUser().getProfileId(), capeId));
						}
					})
					.setTooltip(tooltip("cape"))
					.build()
			);
		}
	}

	public static Screen buildScreen(Screen parent) {
		var configBuilder = ConfigBuilder.create().setParentScreen(parent).setTitle(text("component.title"));
		configBuilder.setSavingRunnable(FrozenLibConfig.INSTANCE::save);
		var config = configBuilder.getOrCreateCategory(text("config"));
		ConfigEntryBuilder entryBuilder = configBuilder.entryBuilder();
		setupEntries(config, entryBuilder);
		return configBuilder.build();
	}

	@Contract(value = "_ -> new", pure = true)
	public static @NotNull Component text(String key) {
		return Component.translatable("option." + FrozenLibConstants.MOD_ID + "." + key);
	}

	@Contract(value = "_ -> new", pure = true)
	public static @NotNull Component tooltip(String key) {
		return Component.translatable("tooltip." + FrozenLibConstants.MOD_ID + "." + key);
	}
}
