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

package net.frozenblock.lib.config.clothconfig;

import java.util.Arrays;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.AbstractFieldBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.config.clothconfig.impl.DisableableWidgetInterface;
import net.frozenblock.lib.config.v2.entry.ConfigEntry;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public final class FrozenClothConfig {

	private FrozenClothConfig() {}

	/**
	 * Creates a subcategory in the parent config source with the specified key and adds entries to it.
	 *
	 * @param entryBuilder the ConfigEntryBuilder instance
	 * @param parentCategory the parent config source
	 * @param key the key for the subcategory
	 * @param expanded if the subcategory is expanded or not
	 * @param tooltip the tooltip for the subcategory
	 * @param entries the entries to be added to the subcategory
	 * @return the newly created subcategory
	 */
	@SuppressWarnings("rawtypes")
	public static ConfigCategory createSubCategory(ConfigEntryBuilder entryBuilder, ConfigCategory parentCategory, Component key, boolean expanded, Component tooltip, AbstractConfigListEntry... entries) {
		// Create the subcategory
		final var subCategory = entryBuilder.startSubCategory(key, Arrays.stream(entries).toList());

		// Set the expanded status
		subCategory.setExpanded(expanded);
		// If the tooltip is not null, set the tooltip for the subcategory
		if (tooltip != null) subCategory.setTooltip(tooltip);

		// Add the subcategory to the parent source and return it
		return parentCategory.addEntry(entryBuilder.startSubCategory(key, Arrays.stream(entries).toList())
			.setExpanded(expanded)
			.setTooltip(tooltip)
			.build()
		);
	}

	/**
	 * Creates an entry that will interact with config syncing
	 *
	 * @param builder The config entry builder to be used
	 * @param configEntry The FrozenLib {@link ConfigEntry}
	 * @since 2.4
	 */
	public static <T, A extends AbstractConfigListEntry<T>, B extends AbstractFieldBuilder<T, A, B>> A entry(B builder, ConfigEntry<T> configEntry) {
		builder.setDefaultValue(configEntry.defaultValue());
		builder.setSaveConsumer(configEntry::setValue);
		return builder.build();
	}

	/**
	 * Creates an entry that will interact with config syncing
	 *
	 * @param builder The config entry builder to be used
	 * @param configEntry The FrozenLib {@link ConfigEntry}
	 * @since 2.4
	 */
	public static <T, A extends AbstractConfigListEntry<T>, B extends AbstractFieldBuilder<T, A, B>> A syncedEntry(B builder, ConfigEntry<T> configEntry) {
		builder.setDefaultValue(configEntry.defaultValue());
		builder.setSaveConsumer(configEntry::setValue);
		final A entry = builder.build();
		((DisableableWidgetInterface) entry).frozenLib$addSyncData(configEntry);
		return entry;
	}
}
