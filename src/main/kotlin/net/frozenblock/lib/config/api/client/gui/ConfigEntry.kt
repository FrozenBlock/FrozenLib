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

package net.frozenblock.lib.config.api.client.gui

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder
import me.shedaniel.clothconfig2.api.Requirement
import me.shedaniel.clothconfig2.impl.builders.AbstractFieldBuilder
import net.minecraft.network.chat.Component

interface ConfigEntry<T> {

    fun <A, B : AbstractConfigListEntry<A>, C : AbstractFieldBuilder<A, B, C>> makeEntry(
        entryBuilder: ConfigEntryBuilder,
        title: Component,
        defaultValue: A,
        saveConsumer: Any,
        tooltip: Component?,
        requiresRestart: Boolean?,
        requirement: Requirement?
    ): C
}
