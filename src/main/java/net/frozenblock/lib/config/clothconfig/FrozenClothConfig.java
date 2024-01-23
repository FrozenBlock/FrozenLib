/*
 * Copyright 2023-2024 FrozenBlock
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

package net.frozenblock.lib.config.clothconfig;

import java.util.Arrays;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.widget.DynamicEntryListWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.clothconfig.impl.DisableableWidgetInterface;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public final class FrozenClothConfig {
	private FrozenClothConfig() {}

	/**
	 * Creates a subcategory in the parent config category with the specified key and adds entries to it.
	 *
	 * @param entryBuilder the ConfigEntryBuilder instance
	 * @param parentCategory the parent config category
	 * @param key the key for the subcategory
	 * @param expanded if the subcategory is expanded or not
	 * @param tooltip the tooltip for the subcategory
	 * @param entries the entries to be added to the subcategory
	 * @return the newly created subcategory
	 */
	@SuppressWarnings("rawtypes")
	public static ConfigCategory createSubCategory(@NotNull ConfigEntryBuilder entryBuilder, @NotNull ConfigCategory parentCategory, @NotNull Component key, boolean expanded, Component tooltip, @NotNull AbstractConfigListEntry... entries) {
		// Create the subcategory
		var subCategory = entryBuilder.startSubCategory(key, Arrays.stream(entries).toList());

		// Set the expanded status
		subCategory.setExpanded(expanded);
		// If the tooltip is not null, set the tooltip for the subcategory
		if (tooltip != null) {
			subCategory.setTooltip(tooltip);
		}

		// Add the subcategory to the parent category and return it
		return parentCategory.addEntry(entryBuilder.startSubCategory(key, Arrays.stream(entries).toList())
				.setExpanded(expanded)
				.setTooltip(tooltip)
				.build()
		);
	}

	/**
	 * Creates an entry that will interact with config syncing
	 *
	 * @param entry The config entry to be used
	 * @param clazz The class of the config file being accessed
	 * @param identifier The identifier of the field used for the config (Use {@link net.frozenblock.lib.config.api.sync.annotation.EntrySyncData} for this)
	 * @param configInstance The main instance of the config (See {@link net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig#INSTANCE} for an example)
	 * @since 1.5
	 */
	public static <T extends DynamicEntryListWidget.Entry<?>> T syncedEntry(T entry, Class<?> clazz, String identifier, Config<?> configInstance) {
		((DisableableWidgetInterface) entry).frozenLib$addSyncData(clazz, identifier, configInstance);
		return entry;
	}
}
