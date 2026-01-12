/*
 * Copyright (C) 2024-2026 FrozenBlock
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
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.cape.api.CapeUtil;
import net.frozenblock.lib.cape.client.api.ClientCapeUtil;
import net.frozenblock.lib.cape.impl.Cape;
import net.frozenblock.lib.cape.impl.networking.CapeCustomizePacket;
import net.frozenblock.lib.config.clothconfig.FrozenClothConfig;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.frozenblock.lib.resource_pack.api.client.FrozenLibModResourcePackApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Contract;

@Environment(EnvType.CLIENT)
public final class FrozenLibConfigGui {

	private static void setupEntries(ConfigCategory category, ConfigEntryBuilder entryBuilder) {
		var useWindOnNonFrozenServers = category.addEntry(
			FrozenClothConfig.syncedEntry(
				entryBuilder.startBooleanToggle(text("use_wind_on_non_frozenlib_servers"), FrozenLibConfig.USE_WIND_ON_NON_FROZEN_SERVERS.getWithSync())
					.setTooltip(tooltip("use_wind_on_non_frozenlib_servers")),
					FrozenLibConfig.USE_WIND_ON_NON_FROZEN_SERVERS
				)
		);

		var saveItemCooldowns = category.addEntry(
			FrozenClothConfig.syncedEntry(
				entryBuilder.startBooleanToggle(text("save_item_cooldowns"), FrozenLibConfig.SAVE_ITEM_COOLDOWNS.getWithSync())
					.setTooltip(tooltip("save_item_cooldowns")),
					FrozenLibConfig.SAVE_ITEM_COOLDOWNS
				)
		);

		var removeExperimentalWarning = category.addEntry(
			FrozenClothConfig.syncedEntry(
				entryBuilder.startBooleanToggle(text("remove_experimental_warning"), FrozenLibConfig.REMOVE_EXPERIMENTAL_WARNING.getWithSync())
					.setTooltip(tooltip("remove_experimental_warning")),
					FrozenLibConfig.REMOVE_EXPERIMENTAL_WARNING
				)
		);

		var wardenSpawnTrackerCommand = category.addEntry(
			FrozenClothConfig.syncedEntry(
				entryBuilder.startBooleanToggle(text("warden_spawn_tracker_command"), FrozenLibConfig.WARDEN_SPAWN_TRACKER_COMMAND.getWithSync())
					.setTooltip(tooltip("warden_spawn_tracker_command")),
					FrozenLibConfig.WARDEN_SPAWN_TRACKER_COMMAND
				)
		);

		var fileTransferServer = category.addEntry(
			FrozenClothConfig.syncedEntry(
				entryBuilder.startBooleanToggle(text("file_transfer_server"), FrozenLibConfig.FILE_TRANSFER_SERVER.get())
					.setTooltip(tooltip("file_transfer_server")),
				FrozenLibConfig.FILE_TRANSFER_SERVER
			)
		);

		var fileTransferClient = category.addEntry(
			FrozenClothConfig.syncedEntry(
				entryBuilder.startBooleanToggle(text("file_transfer_client"), FrozenLibConfig.FILE_TRANSFER_CLIENT.get())
					.setTooltip(tooltip("file_transfer_client")),
				FrozenLibConfig.FILE_TRANSFER_CLIENT
			)
		);

		var packDownloading = category.addEntry(
			FrozenClothConfig.syncedEntry(
				entryBuilder.startEnumSelector(text("pack_downloading"), FrozenLibModResourcePackApi.PackDownloadSetting.class, FrozenLibConfig.PACK_DOWNLOADING.get())
					.setEnumNameProvider(downloadSetting -> enumNameProvider(downloadSetting.toString()))
					.setTooltip(tooltip("pack_downloading")),
				FrozenLibConfig.PACK_DOWNLOADING
			)
		);

		var disabledDataFixTypes = FrozenClothConfig.syncedEntry(
			entryBuilder.startStrList(text("disabled_datafix_types"), FrozenLibConfig.DISABLED_DATA_FIX_TYPES.get())
				.setTooltip(tooltip("disabled_datafix_types"))
				.requireRestart(),
			FrozenLibConfig.DISABLED_DATA_FIX_TYPES
		);

		var datafixerCategory = FrozenClothConfig.createSubCategory(entryBuilder, category, text("datafixer"),
			false,
			tooltip("datafixer"),
			disabledDataFixTypes
		);

		List<String> usableCapes = ClientCapeUtil.getUsableCapes(true).stream().map(cape -> cape.id().toString()).toList();
		if (usableCapes.size() > 1) {
			var capeEntry = category.addEntry(
				entryBuilder.startSelector(text("cape"), usableCapes.toArray(), FrozenLibConfig.CAPE.getWithSync())
					.setDefaultValue(FrozenLibConfig.CAPE.defaultValue())
					.setNameProvider(o -> {
						Identifier capeId = Identifier.parse(((String) o));
						return CapeUtil.getCape(capeId).map(Cape::name).orElse(Component.translatable("cape.frozenlib.invalid"));
					})
					.setSaveConsumer(newValue -> {
						Identifier capeId = Identifier.parse((String) newValue);
						FrozenLibConfig.CAPE.setValue((String) newValue);
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
		configBuilder.setSavingRunnable(FrozenLibConfig.CONFIG::save);
		var config = configBuilder.getOrCreateCategory(text("config"));
		ConfigEntryBuilder entryBuilder = configBuilder.entryBuilder();
		setupEntries(config, entryBuilder);
		return configBuilder.build();
	}

	@Contract(value = "_ -> new", pure = true)
	public static Component text(String key) {
		return Component.translatable("option." + FrozenLibConstants.MOD_ID + "." + key);
	}

	@Contract(value = "_ -> new", pure = true)
	public static Component tooltip(String key) {
		return Component.translatable("tooltip." + FrozenLibConstants.MOD_ID + "." + key);
	}

	@Contract(value = "_ -> new", pure = true)
	public static Component enumNameProvider(String key) {
		return Component.translatable("enum." + FrozenLibConstants.MOD_ID + "." + key);
	}
}
