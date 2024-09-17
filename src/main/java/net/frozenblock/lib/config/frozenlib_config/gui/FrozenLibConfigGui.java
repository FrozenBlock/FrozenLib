/*
 * Copyright (C) 2024 FrozenBlock
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.cape.api.CapeRegistry;
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

		UUID playerUUID = Minecraft.getInstance().getUser().getProfileId();
		List<Cape> usableCapes = new ArrayList<>();
		CapeRegistry.getCapes().forEach(cape -> {
			if (CapeRegistry.canPlayerUserCape(playerUUID, cape.texture())) {
				usableCapes.add(cape);
			}
		});
		if (!usableCapes.isEmpty()) {
			var capeEntry = category.addEntry(
				FrozenClothConfig.syncedEntry(
					entryBuilder.startSelector(text("cape"), usableCapes.toArray(), modifiedConfig.cape)
						.setDefaultValue(defaultConfig.cape)
						.setNameProvider(o -> {
							ResourceLocation capeName = ((Cape) o).location();
							Component component;
							if (capeName == null) {
								component = Component.translatable("cape.frozenlib.none");
							} else {
								component = Component.translatable("cape." + capeName.getNamespace() + "." + capeName.getPath());
							}
							return component;
						})
						.setSaveConsumer(newValue -> {
							if (newValue instanceof Cape cape) {
								config.cape = cape;
								if (Minecraft.getInstance().getConnection() != null) {
									ClientPlayNetworking.send(CapeCustomizePacket.createPacket(playerUUID, cape.texture()));
								}
							}
						})
						.setTooltip(tooltip("cape"))
						.build(),
					config.getClass(),
					"cape",
					configInstance
				)
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
		return Component.translatable("option." + FrozenSharedConstants.MOD_ID + "." + key);
	}

	@Contract(value = "_ -> new", pure = true)
	public static @NotNull Component tooltip(String key) {
		return Component.translatable("tooltip." + FrozenSharedConstants.MOD_ID + "." + key);
	}
}
