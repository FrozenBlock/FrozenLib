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

package net.frozenblock.lib.config.api.client.gui

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder
import me.shedaniel.clothconfig2.api.Requirement
import net.frozenblock.lib.config.api.client.gui.EntryBuilder.Companion.consumerError
import net.frozenblock.lib.config.api.client.gui.EntryBuilder.Companion.defaultValueError
import net.minecraft.network.chat.Component
import java.util.function.Consumer
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

data class EnumEntry<T : Enum<T>>(
    @JvmField val `class`: KClass<T>,
    @JvmField val value: T
) : ConfigEntry<EnumEntry<T>> {

    override fun makeEntry(
        entryBuilder: ConfigEntryBuilder,
        title: Component,
        defaultValue: Any,
        saveConsumer: Any,
        tooltip: Component?,
        requiresRestart: Boolean?,
        requirement: Requirement?
    ): AbstractConfigListEntry<*> {
        val consumer: Consumer<EnumEntry<T>> = saveConsumer as? Consumer<EnumEntry<T>> ?: consumerError()
        val default: EnumEntry<T> = defaultValue as? EnumEntry<T> ?: defaultValueError()

        return entryBuilder.startEnumSelector(title, default.`class`.java, value)
            .setDefaultValue(default.value)
            .setSaveConsumer { newValue -> consumer.accept(EnumEntry(default.`class`, newValue)) }
            .apply {
                tooltip?.let { this.setTooltip(it) }
                requiresRestart?.let { requiresRestart -> this.requireRestart(requiresRestart) }
                requirement?.let { requirement -> this.setRequirement(requirement) }
            }
            .build()
    }

    override fun toString(): String = "EnumEntry[class=$`class`, value=$value]"
}

data class SelectorEntry<T>(
    @JvmField val valuesArray: Array<T>,
    @JvmField val value: T
) : ConfigEntry<SelectorEntry<T>> {

    override fun makeEntry(
        entryBuilder: ConfigEntryBuilder,
        title: Component,
        defaultValue: Any,
        saveConsumer: Any,
        tooltip: Component?,
        requiresRestart: Boolean?,
        requirement: Requirement?
    ): AbstractConfigListEntry<*> {
        val consumer: Consumer<SelectorEntry<T>> = saveConsumer as? Consumer<SelectorEntry<T>> ?: consumerError()
        val default: SelectorEntry<T> = defaultValue as? SelectorEntry<T> ?: defaultValueError()

        return entryBuilder.startSelector(title, valuesArray, value)
            .setDefaultValue(default.value)
            .setSaveConsumer { newValue -> consumer.accept(SelectorEntry(default.valuesArray, newValue)) }
            .apply {
                tooltip?.let { tooltip -> this.setTooltip(tooltip) }
                requiresRestart?.let { requiresRestart -> this.requireRestart(requiresRestart) }
                requirement?.let { requirement -> this.setRequirement(requirement) }
            }
            .build()
    }

    override fun toString(): String = "SelectorEntry[valuesArray=$valuesArray, value=$value]"
}
