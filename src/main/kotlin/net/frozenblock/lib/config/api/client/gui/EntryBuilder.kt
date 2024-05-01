/*
 * Copyright 2024 The Quilt Project
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

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder
import me.shedaniel.clothconfig2.api.Requirement
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

    @JvmField val defaultValue: T & Any , // not nullable

    @JvmField val saveConsumer: Consumer<T>,

    @JvmField val tooltip: Component? = null,

    @JvmField val requiresRestart: Boolean? = false,

    /**
     * @since 1.4
     */
    @JvmField val requirement: Requirement? = null
) {
    companion object {
        private const val CONSUMER_ERROR = "Invalid consumer"
        private const val DEFAULT_VALUE_ERROR = "Invalid default value"

        internal fun consumerError(): Nothing = throw IllegalArgumentException(CONSUMER_ERROR)

        internal fun defaultValueError(): Nothing = throw IllegalArgumentException(DEFAULT_VALUE_ERROR)
    }

    /**
     * @throws IllegalArgumentException if the type of [saveConsumer] is not the same as the type of [value]
     */
    @Suppress("UNCHECKED_CAST")
    fun build(entryBuilder: ConfigEntryBuilder): AbstractConfigListEntry<out Any> {
        val usedValue: T = value ?: defaultValue
        return when (usedValue) {
            is Boolean -> {
                val consumer = saveConsumer as? Consumer<Boolean> ?: consumerError()
                entryBuilder.startBooleanToggle(title, usedValue)
                    .setDefaultValue(defaultValue as Boolean)
                    .setSaveConsumer(consumer)
                    .setYesNoTextSupplier { bool: Boolean -> text(bool.toString()) }
                    .apply {
                        tooltip?.let { tooltip -> this.setTooltip(tooltip) }
                        requiresRestart?.let { requiresRestart -> this.requireRestart(requiresRestart) }
                        requirement?.let { requirement -> this.setRequirement(requirement) }
                    }.build()
            }
            is Int -> {
                val consumer = saveConsumer as? Consumer<Int> ?: consumerError()
                entryBuilder.startIntField(title, usedValue)
                    .setDefaultValue(defaultValue as Int)
                    .setSaveConsumer(consumer)
                    .apply {
                        tooltip?.let { tooltip -> this.setTooltip(tooltip) }
                        requiresRestart?.let { requiresRestart -> this.requireRestart(requiresRestart) }
                        requirement?.let { requirement -> this.setRequirement(requirement) }
                    }.build()
            }
            is Long -> {
                val consumer = saveConsumer as? Consumer<Long> ?: consumerError()
                entryBuilder.startLongField(title, usedValue)
                    .setDefaultValue(defaultValue as Long)
                    .setSaveConsumer(consumer)
                    .apply {
                        tooltip?.let { tooltip -> this.setTooltip(tooltip) }
                        requiresRestart?.let { requiresRestart -> this.requireRestart(requiresRestart) }
                        requirement?.let { requirement -> this.setRequirement(requirement) }
                    }.build()
            }
            is Float -> {
                val consumer = saveConsumer as? Consumer<Float> ?: consumerError()
                entryBuilder.startFloatField(title, usedValue)
                    .setDefaultValue(defaultValue as Float)
                    .setSaveConsumer(consumer)
                    .apply {
                        tooltip?.let { tooltip -> this.setTooltip(tooltip) }
                        requiresRestart?.let { requiresRestart -> this.requireRestart(requiresRestart) }
                        requirement?.let { requirement -> this.setRequirement(requirement) }
                    }.build()
            }
            is Double -> {
                val consumer = saveConsumer as? Consumer<Double> ?: consumerError()
                entryBuilder.startDoubleField(title, usedValue)
                    .setDefaultValue(defaultValue as Double)
                    .setSaveConsumer(consumer)
                    .apply {
                        tooltip?.let { tooltip -> this.setTooltip(tooltip) }
                        requiresRestart?.let { requiresRestart -> this.requireRestart(requiresRestart) }
                        requirement?.let { requirement -> this.setRequirement(requirement) }
                    }.build()
            }
            is String -> {
                val consumer = saveConsumer as? Consumer<String> ?: consumerError()
                entryBuilder.startStrField(title, usedValue)
                    .setDefaultValue(defaultValue as String)
                    .setSaveConsumer(consumer)
                    .apply {
                        tooltip?.let { tooltip -> this.setTooltip(tooltip) }
                        requiresRestart?.let { requiresRestart -> this.requireRestart(requiresRestart) }
                        requirement?.let { requirement -> this.setRequirement(requirement) }
                    }.build()
            }
            is Color -> {
                val consumer = saveConsumer as? Consumer<Color> ?: consumerError()
                entryBuilder.startColorField(title, usedValue.color)
                    .setDefaultValue((defaultValue as Color).color)
                    .setSaveConsumer { color -> consumer.accept(Color(color))}
                    .apply {
                        tooltip?.let { tooltip -> this.setTooltip(tooltip) }
                        requiresRestart?.let { requiresRestart -> this.requireRestart(requiresRestart) }
                        requirement?.let { requirement -> this.setRequirement(requirement) }
                    }.build()
            }
            is Slider<*> -> {
                when (usedValue.type) {
                    SliderType.INT -> {
                        val consumer = saveConsumer as? Consumer<Slider<Int>> ?: consumerError()
                        return entryBuilder.startIntSlider(title, usedValue.value.toInt(), usedValue.min.toInt(), usedValue.max.toInt())
                            .setDefaultValue((defaultValue as Slider<Int>).value.toInt())
                            .setSaveConsumer { newValue -> consumer.accept(Slider(newValue, usedValue.min.toInt(), usedValue.max.toInt(), SliderType.INT))}
                            .apply {
                                tooltip?.let { tooltip -> this.setTooltip(tooltip) }
                                requiresRestart?.let { requiresRestart -> this.requireRestart(requiresRestart) }
                                requirement?.let { requirement -> this.setRequirement(requirement) }
                            }.build()
                    }
                    SliderType.LONG -> {
                        val consumer = saveConsumer as? Consumer<Slider<Long>> ?: consumerError()
                        return entryBuilder.startLongSlider(title, usedValue.value.toLong(), usedValue.min.toLong(), usedValue.max.toLong())
                            .setDefaultValue((defaultValue as Slider<Long>).value.toLong())
                            .setSaveConsumer { newValue -> consumer.accept(Slider<Long>(newValue, usedValue.min.toLong(), usedValue.max.toLong(), SliderType.LONG)) }
                            .apply {
                                tooltip?.let { tooltip -> this.setTooltip(tooltip) }
                                requiresRestart?.let { requiresRestart -> this.requireRestart(requiresRestart) }
                                requirement?.let { requirement -> this.setRequirement(requirement) }
                            }.build()
                    }
                    else -> throw IllegalArgumentException("Unsupported slider type: ${usedValue.type}")
                }
            }
            is StringList -> {
                val consumer = saveConsumer as? Consumer<StringList> ?: consumerError()
                entryBuilder.startStrList(title, usedValue.list)
                    .setDefaultValue((defaultValue as StringList).list)
                    .setSaveConsumer { strList -> consumer.accept(StringList(strList)) }
                    .apply {
                        tooltip?.let { tooltip -> this.setTooltip(tooltip) }
                        requiresRestart?.let { requiresRestart -> this.requireRestart(requiresRestart) }
                        requirement?.let { requirement -> this.setRequirement(requirement) }
                    }
                    .build()
            }
            is IntList -> {
                val consumer = saveConsumer as? Consumer<IntList> ?: consumerError()
                entryBuilder.startIntList(title, usedValue.list)
                    .setDefaultValue((defaultValue as IntList).list)
                    .setSaveConsumer { intList -> consumer.accept(IntList(intList)) }
                    .apply {
                        tooltip?.let { tooltip -> this.setTooltip(tooltip) }
                        requiresRestart?.let { requiresRestart -> this.requireRestart(requiresRestart) }
                        requirement?.let { requirement -> this.setRequirement(requirement) }
                    }
                    .build()
            }
            is LongList -> {
                val consumer = saveConsumer as? Consumer<LongList> ?: consumerError()
                entryBuilder.startLongList(title, usedValue.list)
                    .setDefaultValue((defaultValue as LongList).list)
                    .setSaveConsumer { longList -> consumer.accept(LongList(longList)) }
                    .apply {
                        tooltip?.let { tooltip -> this.setTooltip(tooltip) }
                        requiresRestart?.let { requiresRestart -> this.requireRestart(requiresRestart) }
                        requirement?.let { requirement -> this.setRequirement(requirement) }
                    }
                    .build()
            }
            is FloatList -> {
                val consumer = saveConsumer as? Consumer<FloatList> ?: consumerError()
                entryBuilder.startFloatList(title, usedValue.list)
                    .setDefaultValue((defaultValue as FloatList).list)
                    .setSaveConsumer { floatList -> consumer.accept(FloatList(floatList)) }
                    .apply {
                        tooltip?.let { tooltip -> this.setTooltip(tooltip) }
                        requiresRestart?.let { requiresRestart -> this.requireRestart(requiresRestart) }
                        requirement?.let { requirement -> this.setRequirement(requirement) }
                    }
                    .build()
            }
            is ConfigEntry<*> -> {
                val consumer = saveConsumer as? Consumer<ConfigEntry<*>> ?: consumerError()
                usedValue.makeEntry(entryBuilder, title, defaultValue, consumer, tooltip, requiresRestart, requirement)
            }
            else -> throw IllegalArgumentException("Unsupported type: ${usedValue!!::class.java}")
        }
    }
}
