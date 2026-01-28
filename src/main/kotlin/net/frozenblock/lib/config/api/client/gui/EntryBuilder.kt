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
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.frozenblock.lib.config.frozenlib_config.gui.FrozenLibConfigGui.text
import net.minecraft.network.chat.Component
import java.util.function.Consumer

/**
 * Should only be used if Fabric Language Kotlin is installed.
 * @since 1.3.8
 */
@Environment(EnvType.CLIENT)
data class EntryBuilder<T>(
    @JvmField val title: Component,

    @JvmField val value: T?,

    @JvmField val defaultValue: T? = null,

    @JvmField val saveConsumer: Consumer<T>? = null,

    @JvmField val tooltip: Component? = null,

    @JvmField val requiresRestart: Boolean? = false,

    /**
     * @since 1.4
     */
    @JvmField val requirement: Requirement? = null
) {
    companion object {
        private const val DEFAULT_VALUE_ERROR = "Invalid default value"

        internal fun defaultValueError(): Nothing = throw IllegalArgumentException(DEFAULT_VALUE_ERROR)
    }

    /**
     * @throws IllegalArgumentException if the type of [saveConsumer] is not the same as the type of [value]
     */
    @Suppress("UNCHECKED_CAST")
    fun builder(entryBuilder: ConfigEntryBuilder): AbstractFieldBuilder<*, *, *> {
        val usedValue: T? = value ?: defaultValue
        when (usedValue) {
            is Boolean -> {
                val consumer = saveConsumer as? Consumer<Boolean>
                return entryBuilder.startBooleanToggle(title, usedValue)
                    .setDefaultValue(defaultValue as? Boolean)
                    .setSaveConsumer(consumer)
                    .setYesNoTextSupplier { bool: Boolean -> text(bool.toString()) }
                    .apply {
                        tooltip?.let { tooltip -> this.setTooltip(tooltip) }
                        requiresRestart?.let { requiresRestart -> this.requireRestart(requiresRestart) }
                        requirement?.let { requirement -> this.setRequirement(requirement) }
                    }
            }

            is Int -> {
                val consumer = saveConsumer as? Consumer<Int>
                return entryBuilder.startIntField(title, usedValue)
                    .setDefaultValue(defaultValue as? Int)
                    .setSaveConsumer(consumer)
                    .apply {
                        tooltip?.let { tooltip -> this.setTooltip(tooltip) }
                        requiresRestart?.let { requiresRestart -> this.requireRestart(requiresRestart) }
                        requirement?.let { requirement -> this.setRequirement(requirement) }
                    }
            }

            is Long -> {
                val consumer = saveConsumer as? Consumer<Long>
                return entryBuilder.startLongField(title, usedValue)
                    .setDefaultValue(defaultValue as? Long)
                    .setSaveConsumer(consumer)
                    .apply {
                        tooltip?.let { tooltip -> this.setTooltip(tooltip) }
                        requiresRestart?.let { requiresRestart -> this.requireRestart(requiresRestart) }
                        requirement?.let { requirement -> this.setRequirement(requirement) }
                    }
            }

            is Float -> {
                val consumer = saveConsumer as? Consumer<Float>
                return entryBuilder.startFloatField(title, usedValue)
                    .setDefaultValue(defaultValue as? Float)
                    .setSaveConsumer(consumer)
                    .apply {
                        tooltip?.let { tooltip -> this.setTooltip(tooltip) }
                        requiresRestart?.let { requiresRestart -> this.requireRestart(requiresRestart) }
                        requirement?.let { requirement -> this.setRequirement(requirement) }
                    }
            }

            is Double -> {
                val consumer = saveConsumer as? Consumer<Double>
                return entryBuilder.startDoubleField(title, usedValue)
                    .setDefaultValue(defaultValue as? Double)
                    .setSaveConsumer(consumer)
                    .apply {
                        tooltip?.let { tooltip -> this.setTooltip(tooltip) }
                        requiresRestart?.let { requiresRestart -> this.requireRestart(requiresRestart) }
                        requirement?.let { requirement -> this.setRequirement(requirement) }
                    }
            }

            is String -> {
                val consumer = saveConsumer as? Consumer<String>
                return entryBuilder.startStrField(title, usedValue)
                    .setDefaultValue(defaultValue as? String)
                    .setSaveConsumer(consumer)
                    .apply {
                        tooltip?.let { tooltip -> this.setTooltip(tooltip) }
                        requiresRestart?.let { requiresRestart -> this.requireRestart(requiresRestart) }
                        requirement?.let { requirement -> this.setRequirement(requirement) }
                    }
            }

            is Color -> {
                val consumer = saveConsumer as? Consumer<Color>
                return entryBuilder.startColorField(title, usedValue.color)
                    .setDefaultValue((defaultValue as? Color)?.color)
                    .setSaveConsumer { color -> consumer?.accept(Color(color))}
                    .apply {
                        tooltip?.let { tooltip -> this.setTooltip(tooltip) }
                        requiresRestart?.let { requiresRestart -> this.requireRestart(requiresRestart) }
                        requirement?.let { requirement -> this.setRequirement(requirement) }
                    }
            }

            is Slider<*> -> {
                when (usedValue.type) {
                    SliderType.INT -> {
                        val consumer = saveConsumer as? Consumer<Slider<Int>>
                        return entryBuilder.startIntSlider(title, usedValue.value.toInt(), usedValue.min.toInt(), usedValue.max.toInt())
                            .setDefaultValue((defaultValue as? Slider<Int>)?.value?.toInt())
                            .setSaveConsumer { newValue -> consumer?.accept(Slider(newValue, usedValue.min.toInt(), usedValue.max.toInt(), SliderType.INT))}
                            .apply {
                                tooltip?.let { tooltip -> this.setTooltip(tooltip) }
                                requiresRestart?.let { requiresRestart -> this.requireRestart(requiresRestart) }
                                requirement?.let { requirement -> this.setRequirement(requirement) }
                            }
                    }

                    SliderType.LONG -> {
                        val consumer = saveConsumer as? Consumer<Slider<Long>>
                        return entryBuilder.startLongSlider(title, usedValue.value.toLong(), usedValue.min.toLong(), usedValue.max.toLong())
                            .setDefaultValue((defaultValue as? Slider<Long>)?.value?.toLong())
                            .setSaveConsumer { newValue -> consumer?.accept(Slider(newValue, usedValue.min.toLong(), usedValue.max.toLong(), SliderType.LONG)) }
                            .apply {
                                tooltip?.let { tooltip -> this.setTooltip(tooltip) }
                                requiresRestart?.let { requiresRestart -> this.requireRestart(requiresRestart) }
                                requirement?.let { requirement -> this.setRequirement(requirement) }
                            }
                    }

                    else -> throw IllegalArgumentException("Unsupported slider type: ${usedValue.type}")
                }
            }

            is StringList -> {
                val consumer = saveConsumer as? Consumer<StringList>
                return entryBuilder.startStrList(title, usedValue.list)
                    .setDefaultValue((defaultValue as? StringList)?.list)
                    .setSaveConsumer { strList -> consumer?.accept(StringList(strList)) }
                    .apply {
                        tooltip?.let { tooltip -> this.setTooltip(tooltip) }
                        requiresRestart?.let { requiresRestart -> this.requireRestart(requiresRestart) }
                        requirement?.let { requirement -> this.setRequirement(requirement) }
                    }
            }

            is IntList -> {
                val consumer = saveConsumer as? Consumer<IntList>
                return entryBuilder.startIntList(title, usedValue.list)
                    .setDefaultValue((defaultValue as? IntList)?.list)
                    .setSaveConsumer { intList -> consumer?.accept(IntList(intList)) }
                    .apply {
                        tooltip?.let { tooltip -> this.setTooltip(tooltip) }
                        requiresRestart?.let { requiresRestart -> this.requireRestart(requiresRestart) }
                        requirement?.let { requirement -> this.setRequirement(requirement) }
                    }
            }

            is LongList -> {
                val consumer = saveConsumer as? Consumer<LongList>
                return entryBuilder.startLongList(title, usedValue.list)
                    .setDefaultValue((defaultValue as? LongList)?.list)
                    .setSaveConsumer { longList -> consumer?.accept(LongList(longList)) }
                    .apply {
                        tooltip?.let { tooltip -> this.setTooltip(tooltip) }
                        requiresRestart?.let { requiresRestart -> this.requireRestart(requiresRestart) }
                        requirement?.let { requirement -> this.setRequirement(requirement) }
                    }
            }

            is FloatList -> {
                val consumer = saveConsumer as? Consumer<FloatList>
                return entryBuilder.startFloatList(title, usedValue.list)
                    .setDefaultValue((defaultValue as? FloatList)?.list)
                    .setSaveConsumer { floatList -> consumer?.accept(FloatList(floatList)) }
                    .apply {
                        tooltip?.let { tooltip -> this.setTooltip(tooltip) }
                        requiresRestart?.let { requiresRestart -> this.requireRestart(requiresRestart) }
                        requirement?.let { requirement -> this.setRequirement(requirement) }
                    }
            }

            is ConfigEntry<*> -> {
                val consumer = saveConsumer as? Consumer<ConfigEntry<*>>
                return usedValue.makeEntry(entryBuilder, title, defaultValue, consumer, tooltip, requiresRestart, requirement)
            }

            else -> throw IllegalArgumentException("Unsupported type: ${usedValue!!::class.java}")
        }
    }
}
