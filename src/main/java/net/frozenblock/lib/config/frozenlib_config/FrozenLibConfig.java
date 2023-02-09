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

package net.frozenblock.lib.config.frozenlib_config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Category;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.TransitiveObject;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.config.frozenlib_config.getter.FrozenLibConfigValues;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

@Config(name = FrozenMain.MOD_ID)
public class FrozenLibConfig extends PartitioningSerializer.GlobalData {

    @Category("config")
    @TransitiveObject
    public final FrozenLibConfigCategory config = new FrozenLibConfigCategory();

    public static FrozenLibConfig get() {
        if (!FrozenMain.areConfigsInit) {
            AutoConfig.register(FrozenLibConfig.class, PartitioningSerializer.wrap(GsonConfigSerializer::new));
			FrozenMain.areConfigsInit = true;
			FrozenLibConfigValues.CONFIG = new FrozenLibConfigValues.FrozenConfigGetter(
					new FrozenLibConfigValues.ConfigInterface() {
						@Override
						public boolean useWindOnNonFrozenServers() {
							return FrozenLibConfig.get().config.useWindOnNonFrozenServers;
						}

						@Override
						public boolean saveItemCooldowns() {
							return FrozenLibConfig.get().config.saveItemCooldowns;
						}
					}
			);
        }
        return AutoConfig.getConfigHolder(FrozenLibConfig.class).getConfig();
    }

    @Environment(EnvType.CLIENT)
    public static Screen buildScreen(Screen parent) {
        var configBuilder = ConfigBuilder.create().setParentScreen(parent).setTitle(text("component.title"));
        configBuilder.setSavingRunnable(() -> AutoConfig.getConfigHolder(FrozenLibConfig.class).save());
        var config = configBuilder.getOrCreateCategory(text("config"));
        ConfigEntryBuilder entryBuilder = configBuilder.entryBuilder();
        FrozenLibConfigCategory.setupEntries(config, entryBuilder);
        return configBuilder.build();
    }

	public static Component text(String key) {
		return Component.translatable("option." + FrozenMain.MOD_ID + "." + key);
	}

	public static Component tooltip(String key) {
		return Component.translatable("tooltip." + FrozenMain.MOD_ID + "." + key);
	}

}
