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

package net.frozenblock.lib.config.api.client.gui

import kotlinx.serialization.Serializable

/**
 * A wrapper of a value, minimum, and maximum that represents a slider.
 * [<P>]
 * Should only be used if Fabric Language Kotlin is installed.
 * @param value The current [Int] value of the slider
 * @param min The minimum [Int] of the slider
 * @param max The maximum [Int] of the slider
 * @since 1.4
 */
@Serializable
data class Slider(
    val value: Int?,
    val min: Int,
    val max: Int
) {

    override fun toString(): String = "Slider[value=$value, min=$min, max=$max]"
}