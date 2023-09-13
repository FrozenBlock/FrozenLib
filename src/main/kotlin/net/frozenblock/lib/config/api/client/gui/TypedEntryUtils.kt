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

@file:Environment(EnvType.CLIENT)

package net.frozenblock.lib.config.api.client.gui

import me.shedaniel.clothconfig2.api.*
import me.shedaniel.clothconfig2.gui.entries.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.frozenblock.lib.config.api.entry.TypedEntry
import net.minecraft.network.chat.Component
import java.util.*
import java.util.function.BiFunction
import java.util.function.Consumer
import java.util.function.Supplier

/**
 * Should only be used if Fabric Language Kotlin is installed.
 * @since 1.3.8
 */
fun <T> makeTypedEntryList(entryBuilder: ConfigEntryBuilder, title: Component, entrySupplier: Supplier<TypedEntry<List<T>>?>?, defaultValue: Supplier<TypedEntry<List<T>>>, expandedByDefault: Boolean = false, tooltip: Component, setterConsumer: Consumer<TypedEntry<List<T>>>, cellCreator: BiFunction<T, NestedListListEntry<T, AbstractConfigListEntry<T>>, AbstractConfigListEntry<T>>, requiresRestart: Boolean = false): NestedListListEntry<T, AbstractConfigListEntry<T>> {
    val typedEntry: TypedEntry<List<T>> = entrySupplier?.get() ?: defaultValue.get()

    return NestedListListEntry(
        // Name
        title,
        // Value
        typedEntry.value,
        // Expanded By Default
        expandedByDefault,
        // Tooltip Supplier
        {
            Optional.of(arrayOf(tooltip))
        },
        // Save Consumer
        {
                newValue -> setterConsumer.accept(TypedEntry(typedEntry.type, newValue))
        },
        // Default Value
        defaultValue.get()::value,
        // Reset Button
        entryBuilder.resetButtonKey,
        // Delete Button Enabled
        true,
        // Insert In Front
        true,
        // New Cell Creation
        cellCreator
    ).let {it.isRequiresRestart = requiresRestart; it}
}

/**
 * Should only be used if Fabric Language Kotlin is installed.
 * @since 1.3.8
 */
fun <T> makeNestedList(entryBuilder: ConfigEntryBuilder, title: Component, entrySupplier: Supplier<List<T>?>?, defaultValue: Supplier<List<T>>, expandedByDefault: Boolean = false, tooltip: Component, setterConsumer: Consumer<List<T>>, cellCreator: BiFunction<T, NestedListListEntry<T, AbstractConfigListEntry<T>>, AbstractConfigListEntry<T>>, requiresRestart: Boolean = false): NestedListListEntry<T, out AbstractConfigListEntry<T>> {
    val value: List<T> = entrySupplier?.get() ?: defaultValue.get()

    return NestedListListEntry(
        // Name
        title,
        // Value
        value,
        // Expanded By Default
        expandedByDefault,
        // Tooltip Supplier
        {
            Optional.of(arrayOf(tooltip))
        },
        // Save Consumer
        {
                newValue -> setterConsumer.accept(newValue)
        },
        // Default Value
        defaultValue::get,
        // Reset Button
        entryBuilder.resetButtonKey,
        // Delete Button Enabled
        true,
        // Insert In Front
        true,
        // New Cell Creation
        cellCreator
    ).let {it.isRequiresRestart = requiresRestart; it}
}

/**
 * Should only be used if Fabric Language Kotlin is installed.
 * @since 1.3.8
 */
fun <T> makeMultiElementEntry(title: Component, value: T, defaultExpanded: Boolean = true, vararg entries: AbstractConfigListEntry<out Any>, requiresRestart: Boolean = false): MultiElementListEntry<T> =
    MultiElementListEntry(
        title,
        value, // Default Value
        entries.asList(),
        defaultExpanded
    ).let { it.isRequiresRestart = requiresRestart; it }
