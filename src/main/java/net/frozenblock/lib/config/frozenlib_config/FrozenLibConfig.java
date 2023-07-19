package net.frozenblock.lib.config.frozenlib_config;

import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.api.instance.json.JsonConfig;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;
import net.frozenblock.lib.config.frozenlib_config.getter.DefaultFrozenLibConfig;
import java.util.List;

// NOTE: Refrain from using Typed Entries as Cloth Config is used for Mod Menu Integration
public class FrozenLibConfig {

	private static final Config<FrozenLibConfig> INSTANCE = ConfigRegistry.register(
		new JsonConfig<>(
			FrozenMain.MOD_ID,
			FrozenLibConfig.class,
			true
		)
	);

	public boolean useWindOnNonFrozenServers = DefaultFrozenLibConfig.USE_WIND_ON_NON_FROZENLIB_SERVERS;

	public boolean saveItemCooldowns = DefaultFrozenLibConfig.SAVE_ITEM_COOLDOWNS;

	@ConfigEntry.Gui.CollapsibleObject
	public final DataFixerConfig dataFixer = new DataFixerConfig();

	public static FrozenLibConfig get() {
		return INSTANCE.config();
	}

	public static Config<FrozenLibConfig> getConfigInstance() {
		return INSTANCE;
	}

	public static class DataFixerConfig {
		public List<String> disabledDataFixTypes = DefaultFrozenLibConfig.DISABLED_DATAFIX_TYPES;
	}
}
