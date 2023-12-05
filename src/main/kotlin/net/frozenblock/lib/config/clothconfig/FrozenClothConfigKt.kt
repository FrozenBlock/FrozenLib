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

@file:JvmName("FrozenClothConfigKt")

package net.frozenblock.lib.config.clothconfig

import me.shedaniel.clothconfig2.api.DisableableWidget
import me.shedaniel.clothconfig2.impl.builders.FieldBuilder
import net.frozenblock.lib.config.api.instance.Config
import kotlin.reflect.KClass

fun <T : DisableableWidget> T.synced(clazz: KClass<*>, identifier: String, config: Config<*>): T = this.apply {
    FrozenClothConfig.syncedEntry(this, clazz.java, identifier, config)
}
