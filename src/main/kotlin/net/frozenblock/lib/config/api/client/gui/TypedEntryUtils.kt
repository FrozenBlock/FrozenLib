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

@file:Environment(EnvType.CLIENT)
@file:Suppress("experimental")

package net.frozenblock.lib.config.api.client.gui

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder
import me.shedaniel.clothconfig2.api.Requirement
import me.shedaniel.clothconfig2.gui.entries.MultiElementListEntry
import me.shedaniel.clothconfig2.gui.entries.NestedListListEntry
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
fun <T> typedEntryList(
    entryBuilder: ConfigEntryBuilder,
    title: Component,
    entrySupplier: Supplier<TypedEntry<List<T>>?>?,
    defaultValue: Supplier<TypedEntry<List<T>>>,
    expandedByDefault: Boolean = false,
    tooltip: Component,
    setterConsumer: Consumer<TypedEntry<List<T>>>,
    cellCreator: BiFunction<T, NestedListListEntry<T, AbstractConfigListEntry<T>>, AbstractConfigListEntry<T>>,
    requiresRestart: Boolean = false,
    requirement: Requirement? = null
): NestedListListEntry<T, AbstractConfigListEntry<T>> {
    val typedEntry: TypedEntry<List<T>> = entrySupplier?.get() ?: defaultValue.get()

    return NestedListListEntry(
        // Name
        title,
        // Value
        typedEntry.value(),
        // Expanded By Default
        expandedByDefault,
        // Tooltip Supplier
        {
            Optional.of(arrayOf(tooltip))
        },
        // Save Consumer
        {
                newValue -> setterConsumer.accept(TypedEntry(typedEntry.type(), newValue))
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
    ).apply {
        this.isRequiresRestart = requiresRestart
        this.requirement = requirement
    }
}

/**
 * Should only be used if Fabric Language Kotlin is installed.
 * @since 1.3.8
 */
fun <T> nestedList(
    entryBuilder: ConfigEntryBuilder,
    title: Component,
    entrySupplier: Supplier<List<T>?>?,
    defaultValue: Supplier<List<T>>,
    expandedByDefault: Boolean = false,
    tooltip: Component,
    setterConsumer: Consumer<List<T>>,
    cellCreator: BiFunction<T, NestedListListEntry<T, AbstractConfigListEntry<T>>, AbstractConfigListEntry<T>>,
    requiresRestart: Boolean = false,
    requirement: Requirement? = null
): NestedListListEntry<T, out AbstractConfigListEntry<T>> {
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
        cellCreator,
    ).apply {
        this.isRequiresRestart = requiresRestart
        this.requirement = requirement
    }
}

/**
 * Should only be used if Fabric Language Kotlin is installed.
 * @since 1.3.8
 */
fun <T> multiElementEntry(
    title: Component,
    value: T,
    defaultExpanded: Boolean = true,
    vararg entries: AbstractConfigListEntry<out Any>,
    requiresRestart: Boolean = false,
    requirement: Requirement? = null
): MultiElementListEntry<T> =
    MultiElementListEntry(
        title,
        value, // Default Value
        entries.asList(),
        defaultExpanded
    ).apply {
        this.isRequiresRestart = requiresRestart
        this.requirement = requirement
    }
