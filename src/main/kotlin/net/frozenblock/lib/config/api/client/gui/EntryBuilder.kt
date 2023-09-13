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

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.frozenblock.lib.config.frozenlib_config.gui.FrozenLibConfigGui.text as text
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
    @JvmField val defaultValue: T & Any, // not nullable
    @JvmField val saveConsumer: Consumer<T>,
    @JvmField val tooltip: Component? = null,
    @JvmField val requiresRestart: Boolean? = false
) {
    companion object {
        private const val CONSUMER_ERROR = "Invalid consumer"
    }

    /**
     * @throws IllegalArgumentException if the type of [saveConsumer] is not the same as the type of [value]
     */
    @Suppress("UNCHECKED_CAST")
    fun build(entryBuilder: ConfigEntryBuilder): AbstractConfigListEntry<out Any> {
        val usedValue: T = value ?: defaultValue
        return when (usedValue) {
            is Boolean -> {
                val consumer = saveConsumer as? Consumer<Boolean> ?: throw IllegalArgumentException(CONSUMER_ERROR)
                entryBuilder.startBooleanToggle(title, usedValue)
                    .setDefaultValue(defaultValue as Boolean)
                    .setSaveConsumer(consumer)
                    .setYesNoTextSupplier { bool: Boolean -> text(bool.toString()) }
                    .let {
                        tooltip?.let { tooltip -> it.setTooltip(tooltip) }
                        requiresRestart?.let { requiresRestart -> it.requireRestart(requiresRestart) }
                        it
                    }.build()
            }
            is Int -> {
                val consumer = saveConsumer as? Consumer<Int> ?: throw IllegalArgumentException(CONSUMER_ERROR)
                entryBuilder.startIntField(title, usedValue)
                    .setDefaultValue(defaultValue as Int)
                    .setSaveConsumer(consumer)
                    .let {
                        tooltip?.let { tooltip -> it.setTooltip(tooltip) }
                        requiresRestart?.let { requiresRestart -> it.requireRestart(requiresRestart) }
                        it
                    }.build()
            }
            is Long -> {
                val consumer = saveConsumer as? Consumer<Long> ?: throw IllegalArgumentException(CONSUMER_ERROR)
                entryBuilder.startLongField(title, usedValue)
                    .setDefaultValue(defaultValue as Long)
                    .setSaveConsumer(consumer)
                    .let {
                        tooltip?.let { tooltip -> it.setTooltip(tooltip) }
                        requiresRestart?.let { requiresRestart -> it.requireRestart(requiresRestart) }
                        it
                    }.build()
            }
            is Float -> {
                val consumer = saveConsumer as? Consumer<Float> ?: throw IllegalArgumentException(CONSUMER_ERROR)
                entryBuilder.startFloatField(title, usedValue)
                    .setDefaultValue(defaultValue as Float)
                    .setSaveConsumer(consumer)
                    .let {
                        tooltip?.let { tooltip -> it.setTooltip(tooltip) }
                        requiresRestart?.let { requiresRestart -> it.requireRestart(requiresRestart) }
                        it
                    }.build()
            }
            is Double -> {
                val consumer = saveConsumer as? Consumer<Double> ?: throw IllegalArgumentException(CONSUMER_ERROR)
                entryBuilder.startDoubleField(title, usedValue)
                    .setDefaultValue(defaultValue as Double)
                    .setSaveConsumer(consumer)
                    .let {
                        tooltip?.let { tooltip -> it.setTooltip(tooltip) }
                        requiresRestart?.let { requiresRestart -> it.requireRestart(requiresRestart) }
                        it
                    }.build()
            }
            is String -> {
                val consumer = saveConsumer as? Consumer<String> ?: throw IllegalArgumentException(CONSUMER_ERROR)
                entryBuilder.startStrField(title, usedValue)
                    .setDefaultValue(defaultValue as String)
                    .setSaveConsumer(consumer)
                    .let {
                        tooltip?.let { tooltip -> it.setTooltip(tooltip) }
                        requiresRestart?.let { requiresRestart -> it.requireRestart(requiresRestart) }
                        it
                    }.build()
            }
            is Color -> {
                val consumer = saveConsumer as? Consumer<Int> ?: throw IllegalArgumentException(CONSUMER_ERROR)
                entryBuilder.startColorField(title, usedValue.color)
                    .setDefaultValue((defaultValue as Color).color)
                    .setSaveConsumer(consumer)
                    .let {
                        tooltip?.let { tooltip -> it.setTooltip(tooltip) }
                        requiresRestart?.let { requiresRestart -> it.requireRestart(requiresRestart) }
                        it
                    }.build()
            }
            else -> throw IllegalArgumentException("Unsupported type: ${usedValue!!::class.java}")
        }
    }
}
