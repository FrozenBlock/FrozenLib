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

import kotlin.reflect.KClass

/**
 * An [Int] wrapper that represents a color.
 * [<P>]
 * Should only be used if Fabric Language Kotlin is installed.
 * @param color The [Int] representation of the color
 * @since 1.3.8
 */
data class Color(@JvmField val color: Int) {

    override fun toString(): String = "Color[$color]"

}

// lists

data class StringList(@JvmField val list: List<String>) {

    override fun toString(): String = "StringList[$list]"
}

data class IntList(@JvmField val list: List<Int>) {

    override fun toString(): String = "IntList[$list]"
}

data class LongList(@JvmField val list: List<Long>) {

    override fun toString(): String = "LongList[$list]"
}

data class FloatList(@JvmField val list: List<Float>) {

    override fun toString(): String = "FloatList[$list]"
}

data class DoubleList(@JvmField val list: List<Double>) {

    override fun toString(): String = "DoubleList[$list]"
}

// not lists anymore

data class EnumEntry<T : Enum<?>>(
    @JvmField val `class`: KClass<T>,
    @JvmField val value: T
) {

    override fun toString(): String = "EnumEntry[class=$`class`, value=$value]"
}

data class SelectorEntry<T>(
    @JvmField val valuesArray: Array<T>,
    @JvmField val value: T
) {

    override fun toString(): String = "SelectorEntry[valuesArray=$valuesArray, value=$value]"
}