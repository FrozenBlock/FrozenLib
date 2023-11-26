@file:JvmName("FrozenClothConfigKt")

package net.frozenblock.lib.config.clothconfig

import me.shedaniel.clothconfig2.impl.builders.FieldBuilder
import kotlin.reflect.KClass

fun <T : FieldBuilder<*, *, *>> T.synced(clazz: KClass<*>, identifier: String): T = this.apply {
    FrozenClothConfig.makeFieldBuilderWithSyncData(this, clazz.java, identifier)
}
