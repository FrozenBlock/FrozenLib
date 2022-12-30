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
        category.setBackground(FrozenMain.id("textures/config.png"));

        var useWindOnNonFrozenServers = category.addEntry(entryBuilder.startBooleanToggle(text("use_wind_on_non_frozenlib_servers"), config.useWindOnNonFrozenServers)
                .setDefaultValue(FrozenLibConfigValues.DefaultFrozenLibConfigValues.USE_WIND_ON_NON_FROZENLIB_SERVERS)
                .setSaveConsumer(newValue -> config.useWindOnNonFrozenServers = newValue)
                .setTooltip(tooltip("use_wind_on_non_frozenlib_servers"))
                .build()
        );
    }
}
