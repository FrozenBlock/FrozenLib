package net.frozenblock.lib.config.api.client.gui

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder
import me.shedaniel.clothconfig2.api.Requirement
import net.minecraft.network.chat.Component

fun interface ConfigEntry<T> {

    fun makeEntry(
        entryBuilder: ConfigEntryBuilder,
        title: Component,
        defaultValue: Any,
        saveConsumer: Any,
        tooltip: Component?,
        requiresRestart: Boolean?,
        requirement: Requirement?
    ): AbstractConfigListEntry<*>
}
