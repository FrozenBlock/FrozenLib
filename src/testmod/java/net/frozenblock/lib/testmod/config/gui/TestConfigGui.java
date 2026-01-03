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

package net.frozenblock.lib.testmod.config.gui;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.config.api.client.gui.EntryBuilder;
import net.frozenblock.lib.config.api.client.gui.Slider;
import net.frozenblock.lib.config.api.client.gui.SliderType;
import net.frozenblock.lib.config.clothconfig.FrozenClothConfig;
import net.frozenblock.lib.config.newconfig.ConfigSerializer;
import net.frozenblock.lib.testmod.FrozenTestMain;
import net.frozenblock.lib.testmod.config.TestConfig;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public class TestConfigGui {

	@Environment(EnvType.CLIENT)
	public static void setupEntries(ConfigCategory category, ConfigEntryBuilder entryBuilder) {
		var subMenu = TestConfig.subMenu.getActual();
		category.setBackground(Identifier.withDefaultNamespace("textures/block/packed_mud.png"));

		var test = category.addEntry(entryBuilder.startBooleanToggle(text("test_toggle"), TestConfig.testToggle.getActual())
				.setDefaultValue(TestConfig.testToggle.getDefaultValue())
				.setSaveConsumer(TestConfig.testToggle::setValue)
				.setTooltip(tooltip("test_toggle"))
				.build()
		);

		var sliderTest = new EntryBuilder<>(Component.literal("This is wild"), new Slider<>(TestConfig.testInt.getActual(), 0, 100, SliderType.INT.INSTANCE),
			new Slider<>(TestConfig.testInt.getDefaultValue(), 0, 100, SliderType.INT.INSTANCE),
			newValue -> TestConfig.testInt.setValue(newValue.getValue().intValue()),
			null,
			false,
			null
		).build(entryBuilder);

		var testSubMenuBoolean = entryBuilder.startBooleanToggle(text("sub_option"), subMenu.subOption)
				.setDefaultValue(TestConfig.subMenu.getDefaultValue().subOption)
				.setSaveConsumer(newValue -> subMenu.subOption = newValue)
				.setTooltip(tooltip("sub_option"))
				.build();

		var testSubMenuCategory = FrozenClothConfig.createSubCategory(entryBuilder, category, text("test_subcategory"),
				false,
				tooltip("test_subcategory"),
				testSubMenuBoolean, sliderTest
		);
	}

	public static Screen buildScreen(Screen parent) {
		var configBuilder = ConfigBuilder.create().setParentScreen(parent).setTitle(text("component.title"));
		configBuilder.setSavingRunnable(TestConfig.CONFIG::save);
		var config = configBuilder.getOrCreateCategory(text("config"));
		ConfigEntryBuilder entryBuilder = configBuilder.entryBuilder();
		setupEntries(config, entryBuilder);
		return configBuilder.build();
	}

	private static Component text(String key) {
		return Component.translatable("option." + FrozenTestMain.MOD_ID + "." + key);
	}

	private static Component tooltip(String key) {
		return Component.translatable("tooltip." + FrozenTestMain.MOD_ID + "." + key);
	}
}
