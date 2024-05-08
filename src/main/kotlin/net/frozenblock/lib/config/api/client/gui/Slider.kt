/*
 * Copyright 2024 FrozenBlock
 * Copyright 2024 FrozenBlock
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
 */

package net.frozenblock.lib.config.api.client.gui

/**
 * A wrapper of a value, minimum, and maximum that represents a slider.
 * [<P>]
 * Should only be used if Fabric Language Kotlin is installed.
 * @param value The current [Int] value of the slider
 * @param min The minimum [Int] of the slider
 * @param max The maximum [Int] of the slider
 * @since 1.4
 */
data class Slider<T>(
    val value: Number,
    val min: Number,
    val max: Number,
    val type: SliderType<T>
) {

    override fun toString(): String = "Slider[value=$value, min=$min, max=$max]"
}

sealed class SliderType<T> {
    data object INT : SliderType<Int>()

    data object LONG : SliderType<Long>()
}
