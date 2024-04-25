/*
 * Copyright 2023 The Quilt Project
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
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
