/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.testmod.config.gui

import me.shedaniel.clothconfig2.api.ConfigBuilder
import me.shedaniel.clothconfig2.api.ConfigCategory
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.frozenblock.lib.config.api.client.gui.EntryBuilder
import net.frozenblock.lib.config.api.client.gui.Slider
import net.frozenblock.lib.config.api.client.gui.SliderType
import net.frozenblock.lib.config.clothconfig.FrozenClothConfig
import net.frozenblock.lib.config.clothconfig.synced
import net.frozenblock.lib.testmod.FrozenTestMain
import net.frozenblock.lib.testmod.config.TestConfig
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import java.util.function.Consumer

@Environment(EnvType.CLIENT)
object TestConfigGui {
    @Environment(EnvType.CLIENT)
    fun setupEntries(category: ConfigCategory, entryBuilder: ConfigEntryBuilder) {
        category.setBackground(Identifier.withDefaultNamespace("textures/block/packed_mud.png"))

        val test = category.addEntry(
            EntryBuilder<Boolean, Boolean>(
                text("test_toggle"),
                TestConfig.testToggle,
                tooltip("test_toggle"),
            ).build(entryBuilder)
        )

        val sliderTest = EntryBuilder(
            Component.literal("This is wild"),
            TestConfig.testInt,
            Component.literal("Epic tooltip"),
            Slider(TestConfig.testInt.withSync, 0, 100, SliderType.INT),
            Slider(TestConfig.testInt.defaultValue(), 0, 100, SliderType.INT),
        ).build(entryBuilder)

        val testSubMenuBoolean = entryBuilder.startBooleanToggle(text("sub_option"), TestConfig.subOption.withSync)
            .setTooltip(tooltip("sub_option"))
            .synced(TestConfig.subOption)

        val testSubMenuCategory = FrozenClothConfig.createSubCategory(
            entryBuilder, category, text("test_subcategory"),
            false,
            tooltip("test_subcategory"),
            testSubMenuBoolean, sliderTest
        )
    }

    @JvmStatic
	fun buildScreen(parent: Screen?): Screen? {
        val configBuilder = ConfigBuilder.create().setParentScreen(parent).setTitle(text("component.title"))
        configBuilder.setSavingRunnable(TestConfig.CONFIG::save)
        val config = configBuilder.getOrCreateCategory(text("config"))
        val entryBuilder = configBuilder.entryBuilder()
        setupEntries(config, entryBuilder)
        return configBuilder.build()
    }

    private fun text(key: String?): Component {
        return Component.translatable("option." + FrozenTestMain.MOD_ID + "." + key)
    }

    private fun tooltip(key: String?): Component {
        return Component.translatable("tooltip." + FrozenTestMain.MOD_ID + "." + key)
    }
}
