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
import java.util.function.Function;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.Requirement;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.IntegerSliderEntry;
import me.shedaniel.clothconfig2.impl.builders.AbstractFieldBuilder;
import me.shedaniel.clothconfig2.impl.builders.AbstractSliderFieldBuilder;
import me.shedaniel.clothconfig2.impl.builders.BooleanToggleBuilder;
import me.shedaniel.clothconfig2.impl.builders.EnumSelectorBuilder;
import me.shedaniel.clothconfig2.impl.builders.SelectorBuilder;
import net.frozenblock.lib.config.clothconfig.impl.DisableableWidgetInterface;
import net.frozenblock.lib.config.v2.entry.ConfigEntry;
import net.frozenblock.lib.config.v2.entry.property.EntryProperties;
import net.frozenblock.lib.platform.api.ClientOnly;
import net.minecraft.network.chat.Component;

@ClientOnly
public final class FrozenLibClothConfigGuiHelper {

	/**
	 * Creates a subcategory in the parent config source with the specified key and adds entries to it.
	 * <p>
	 * See {@link FrozenLibClothConfigGuiHelper#createSubCategory}.
	 *
	 * @param entryBuilder the ConfigEntryBuilder instance
	 * @param parentCategory the parent config source
	 * @param key the key for the subcategory
	 * @param tooltip the tooltip for the subcategory
	 * @param entries the entries to be added to the subcategory
	 * @return the newly created subcategory
	 */
	@SuppressWarnings("rawtypes")
	public static ConfigCategory createSubCategory(
		ConfigEntryBuilder entryBuilder,
		ConfigCategory parentCategory,
		Component key,
		Component tooltip,
		AbstractConfigListEntry... entries
	) {
		return createSubCategory(entryBuilder, parentCategory, key, false, tooltip, entries);
	}

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
	public static ConfigCategory createSubCategory(
		ConfigEntryBuilder entryBuilder,
		ConfigCategory parentCategory,
		Component key,
		boolean expanded,
		Component tooltip,
		AbstractConfigListEntry... entries
	) {
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
	 * Creates an int slider entry ranging from 0 to 500.
	 *
	 * @param entryBuilder the ConfigEntryBuilder instance
	 * @param key the key to use for the entry's text and tooltip
	 * @param configEntry the {@link ConfigEntry}
	 * @return the newly created int slider entry
	 */
	public static IntegerSliderEntry zeroToFiveHundredEntry(ConfigEntryBuilder entryBuilder, String key, ConfigEntry<Integer> configEntry) {
		return intSliderEntry(entryBuilder, key, configEntry, 0, 500);
	}

	/**
	 * Creates an int slider entry ranging from 2 to 500.
	 *
	 * @param entryBuilder the ConfigEntryBuilder instance
	 * @param key the key to use for the entry's text and tooltip
	 * @param configEntry the {@link ConfigEntry}
	 * @return the newly created int slider entry
	 */
	public static IntegerSliderEntry oneToFiveHundredEntry(ConfigEntryBuilder entryBuilder, String key, ConfigEntry<Integer> configEntry) {
		return intSliderEntry(entryBuilder, key, configEntry, 1, 500);
	}

	/**
	 * Creates an int slider entry ranging from 0 to 1000.
	 *
	 * @param entryBuilder the ConfigEntryBuilder instance
	 * @param key the key to use for the entry's text and tooltip
	 * @param configEntry the {@link ConfigEntry}
	 * @return the newly created int slider entry
	 */
	public static IntegerSliderEntry zeroToOneThousandEntry(ConfigEntryBuilder entryBuilder, String key, ConfigEntry<Integer> configEntry) {
		return intSliderEntry(entryBuilder, key, configEntry, 0, 1000);
	}

	/**
	 * Creates an int slider entry ranging from 1 to 1000.
	 *
	 * @param entryBuilder the ConfigEntryBuilder instance
	 * @param key the key to use for the entry's text and tooltip
	 * @param configEntry the {@link ConfigEntry}
	 * @return the newly created int slider entry
	 */
	public static IntegerSliderEntry oneToOneThousandEntry(ConfigEntryBuilder entryBuilder, String key, ConfigEntry<Integer> configEntry) {
		return intSliderEntry(entryBuilder, key, configEntry, 1, 1000);
	}

	/**
	 * Creates an int slider entry.
	 *
	 * @param entryBuilder the ConfigEntryBuilder instance
	 * @param key the key to use for the entry's text and tooltip
	 * @param configEntry the {@link ConfigEntry}
	 * @param min the minimum allowed value
	 * @param max the maximum allowed value
	 * @return the newly created int slider entry
	 */
	public static IntegerSliderEntry intSliderEntry(ConfigEntryBuilder entryBuilder, String key, ConfigEntry<Integer> configEntry, int min, int max) {
		return syncedEntry(
			entryBuilder.startIntSlider(text(key, configEntry), configEntry.get(), min, max).setTooltip(tooltip(key, configEntry)),
			configEntry
		);
	}

	/**
	 * Creates a boolean entry.
	 *
	 * @param entryBuilder the ConfigEntryBuilder instance
	 * @param key the key to use for the entry's text and tooltip
	 * @param configEntry the {@link ConfigEntry}
	 * @return the newly created int slider entry
	 */
	public static BooleanListEntry booleanEntry(ConfigEntryBuilder entryBuilder, String key, ConfigEntry<Boolean> configEntry) {
		return booleanEntry(entryBuilder, text(key, configEntry), configEntry, tooltip(key, configEntry));
	}

	/**
	 * Creates a boolean entry.
	 *
	 * @param entryBuilder the ConfigEntryBuilder instance
	 * @param name the entry's name
	 * @param configEntry the {@link ConfigEntry}
	 * @param tooltip tooltip, or array of tooltips for the entry
	 * @return the newly created int slider entry
	 */
	public static BooleanListEntry booleanEntry(ConfigEntryBuilder entryBuilder, Component name, ConfigEntry<Boolean> configEntry, Component... tooltip) {
		return syncedEntry(
			entryBuilder.startBooleanToggle(name, configEntry.get()).setTooltip(tooltip),
			configEntry
		);
	}

	/**
	 * Creates an entry from a {@link ConfigEntry}.
	 *
	 * <p>
	 * {@link EntryProperties} will be applied to the new Cloth Config entry.
	 *
	 * @param entryBuilder The config entry builder to be used
	 * @param configEntry The FrozenLib {@link ConfigEntry}
	 * @since 2.4
	 */
	public static <T, A extends AbstractConfigListEntry<T>, B extends AbstractFieldBuilder<T, A, B>> A entry(B entryBuilder, ConfigEntry<T> configEntry) {
		if (entryBuilder.getDefaultValue() == null) entryBuilder.setDefaultValue(configEntry.defaultValue());
		if (entryBuilder.getSaveConsumer() == null) entryBuilder.setSaveConsumer(configEntry::setValue);
		if (configEntry.hasVisibilityPredicate()) entryBuilder.setDisplayRequirement(Requirement.isTrue(configEntry::isVisible));
		if (configEntry.requireRestart()) entryBuilder.requireRestart();

		appendTextSupplier: {
			if (!configEntry.hasTextSupplier()) break appendTextSupplier;
			final Function textSupplier = configEntry.textSupplier().get();
			if (entryBuilder instanceof AbstractSliderFieldBuilder<?, ?, ?> sliderFieldBuilder) sliderFieldBuilder.setTextGetter(textSupplier);
			if (entryBuilder instanceof BooleanToggleBuilder booleanToggleBuilder) booleanToggleBuilder.setYesNoTextSupplier(textSupplier);
			if (entryBuilder instanceof EnumSelectorBuilder<?> enumSelectorBuilder) enumSelectorBuilder.setEnumNameProvider(textSupplier);
			if (entryBuilder instanceof SelectorBuilder<?> selectorBuilder) selectorBuilder.setNameProvider(textSupplier);
		}

		return entryBuilder.build();
	}

	/**
	 * Creates an entry from a {@link ConfigEntry} that will interact with config syncing.
	 *
	 * <p>
	 * {@link EntryProperties} will be applied to the new Cloth Config entry.
	 *
	 * @param entryBuilder The config entry builder to be used
	 * @param configEntry The FrozenLib {@link ConfigEntry}
	 * @since 2.4
	 */
	public static <T, A extends AbstractConfigListEntry<T>, B extends AbstractFieldBuilder<T, A, B>> A syncedEntry(B entryBuilder, ConfigEntry<T> configEntry) {
		final var clothEntry = entry(entryBuilder, configEntry);
		return syncedEntry(clothEntry, configEntry);
	}

	public static <T, A extends AbstractConfigListEntry<T>> A syncedEntry(A clothEntry, ConfigEntry<T> configEntry) {
		((DisableableWidgetInterface) clothEntry).frozenLib$addSyncData(configEntry);
		return clothEntry;
	}

	/**
	 * @return A text component for use in a Config GUI
	 */
	public static Component text(String key, ConfigEntry<?> configEntry, final Object... args) {
		return Component.translatable("option." + configEntry.id().namespace() + "." + key, args);
	}

	/**
	 * @return A tooltip component for use in a Config GUI
	 */
	public static Component tooltip(String key, ConfigEntry<?> configEntry, final Object... args) {
		return Component.translatable("tooltip." + configEntry.id().namespace() + "." + key, args);
	}
}
