/*
 * Copyright 2022 FrozenBlock
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

package net.frozenblock.lib.testmod.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.testmod.FrozenTestMain;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

@Config(name = FrozenTestMain.MOD_ID)
public class TestConfig extends PartitioningSerializer.GlobalData implements ConfigData {

	@ConfigEntry.Category("general")
	@ConfigEntry.Gui.TransitiveObject
	public GeneralTestConfig general = new GeneralTestConfig();

	public static TestConfig get() {
		if (!FrozenTestMain.areConfigsInit) {
			AutoConfig.register(TestConfig.class, PartitioningSerializer.wrap(GsonConfigSerializer::new));
			FrozenTestMain.areConfigsInit = true;
		}
		return AutoConfig.getConfigHolder(TestConfig.class).getConfig();
	}

	public static Component text(String key) {
		return Component.translatable("option." + FrozenTestMain.MOD_ID + "." + key);
	}

	public static Component tooltip(String key) {
		return Component.translatable("tooltip." + FrozenTestMain.MOD_ID + "." + key);
	}

	@Environment(EnvType.CLIENT)
	public static Screen buildScreen(Screen parent) {
		var configBuilder = ConfigBuilder.create().setParentScreen(parent).setTitle(text("component.title"));
		configBuilder.setSavingRunnable(() -> AutoConfig.getConfigHolder(TestConfig.class).save());
		var general = configBuilder.getOrCreateCategory(text("general"));
		ConfigEntryBuilder entryBuilder = configBuilder.entryBuilder();
		GeneralTestConfig.setupEntries(general, entryBuilder);
		return configBuilder.build();
	}


}
