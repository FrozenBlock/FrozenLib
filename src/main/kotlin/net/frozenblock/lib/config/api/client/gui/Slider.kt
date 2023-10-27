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