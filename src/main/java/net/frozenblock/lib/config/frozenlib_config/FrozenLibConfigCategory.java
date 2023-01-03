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

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.FrozenMain;
import static net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig.text;
import static net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig.tooltip;
import net.frozenblock.lib.config.frozenlib_config.getter.FrozenLibConfigValues;

@Config(name = "config")
public final class FrozenLibConfigCategory implements ConfigData {

	public boolean useWindOnNonFrozenServers = FrozenLibConfigValues.DefaultFrozenLibConfigValues.USE_WIND_ON_NON_FROZENLIB_SERVERS;

    @Environment(EnvType.CLIENT)
    static void setupEntries(ConfigCategory category, ConfigEntryBuilder entryBuilder) {
        var config = FrozenLibConfig.get().config;
        category.setBackground(FrozenMain.id("config.png"));

        var useWindOnNonFrozenServers = category.addEntry(entryBuilder.startBooleanToggle(text("use_wind_on_non_frozenlib_servers"), config.useWindOnNonFrozenServers)
                .setDefaultValue(FrozenLibConfigValues.DefaultFrozenLibConfigValues.USE_WIND_ON_NON_FROZENLIB_SERVERS)
                .setSaveConsumer(newValue -> config.useWindOnNonFrozenServers = newValue)
                .setTooltip(tooltip("use_wind_on_non_frozenlib_servers"))
                .build()
        );
    }
}
