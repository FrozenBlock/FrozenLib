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

@file:Environment(EnvType.CLIENT)
@file:Suppress("experimental")

package net.frozenblock.lib.config.api.client.gui

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder
import me.shedaniel.clothconfig2.api.Requirement
import me.shedaniel.clothconfig2.gui.entries.NestedListListEntry
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.frozenblock.lib.config.v2.entry.ConfigEntry
import net.minecraft.network.chat.Component
import java.util.*
import java.util.function.BiFunction

/**
 * Should only be used if Fabric Language Kotlin is installed.
 * Creates a nested list entry for a ConfigEntry containing a list.
 * @since 2.4
 */
fun <T> configEntryList(
    entryBuilder: ConfigEntryBuilder,
    title: Component,
    configEntry: ConfigEntry<List<T>>,
    expandedByDefault: Boolean = false,
    tooltip: Component,
    cellCreator: BiFunction<T, NestedListListEntry<T, AbstractConfigListEntry<T>>, AbstractConfigListEntry<T>>,
    requiresRestart: Boolean = false,
    requirement: Requirement? = null
): NestedListListEntry<T, AbstractConfigListEntry<T>> {
    return NestedListListEntry(
        title,
        configEntry.get(),
        expandedByDefault,
        {
            Optional.of(arrayOf(tooltip))
        },
        configEntry::setValue,
        configEntry::defaultValue,
        entryBuilder.resetButtonKey,
        true,
        true,
        cellCreator
    ).apply {
        this.isRequiresRestart = requiresRestart
        this.requirement = requirement
    }
}
