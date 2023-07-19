package net.frozenblock.lib.config.frozenlib_config.gui;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.config.clothconfig.FrozenClothConfig;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.frozenblock.lib.config.frozenlib_config.defaults.DefaultFrozenLibConfig;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public final class FrozenLibConfigGui {

	private static void setupEntries(ConfigCategory category, ConfigEntryBuilder entryBuilder) {
		var config = FrozenLibConfig.get();
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
		return Component.translatable("option." + FrozenMain.MOD_ID + "." + key);
	}

	public static Component tooltip(String key) {
		return Component.translatable("tooltip." + FrozenMain.MOD_ID + "." + key);
	}
}
