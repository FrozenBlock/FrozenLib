/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.testmod.config

import blue.endless.jankson.Comment
import blue.endless.jankson.annotation.SaveToggle
import net.frozenblock.lib.config.api.entry.TypedEntry
import net.frozenblock.lib.config.api.entry.TypedEntryType
import net.frozenblock.lib.config.api.instance.Config
import net.frozenblock.lib.config.api.instance.xjs.XjsConfig
import net.frozenblock.lib.config.api.instance.xjs.XjsFormat
import net.frozenblock.lib.config.api.registry.ConfigRegistry
import net.frozenblock.lib.testmod.FrozenTestMain
import net.minecraft.core.Holder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.phys.Vec3

private val VEC_LIST_TYPE: TypedEntryType<List<Vec3>> = ConfigRegistry.register(
    TypedEntryType(
        FrozenTestMain.MOD_ID,
        Vec3.CODEC.listOf()
    )
)

private val SOUND_HOLDER_TYPE: TypedEntryType<Holder<SoundEvent>> = ConfigRegistry.register(
    TypedEntryType(
        FrozenTestMain.MOD_ID,
        BuiltInRegistries.SOUND_EVENT.holderByNameCodec()
    )
)

data class TestConfig(
    @JvmField
    @Comment("This is a boolean value.")
    var testToggle: Boolean = true,

    @JvmField
    @Comment("This is an integer value.")
    var testInt: Int = 69,

    @JvmField
    @Comment("This is a long value.")
    var testLong: Long = 69420L,

    @JvmField
    @Comment("This is a float value.")
    var testFloat: Float = 69.420f,

    @JvmField
    @Comment("This is a double value.")
    var testDouble: Double = 69.4206942069420,

    @JvmField
    @Comment("This is an integer list typed entry.")
    var testIntList: List<Int> = listOf(45),

    @JvmField
    @Comment("This is a Vec3 list typed entry.")
    var typedVecList: TypedEntry<List<Vec3>> = TypedEntry.create(
        VEC_LIST_TYPE,
        listOf(
            Vec3(0.0, 0.0, 0.0),
            Vec3(1.0, 1.0, 1.0)
        )
    ),

    @JvmField
    @Comment("This is a list of doubles")
    var doubleList: List<Double> = listOf(1.0, 2.0, 3.0, Math.PI),

    @JvmField
    @Comment("This should not be seen unless config serialization is bugged")
    @SaveToggle(false)
    var mysteriousUnknownToggle: Boolean = false,

    @JvmField
    @Comment("Sub menu!")
    var subMenu: SubMenu = SubMenu(),
) {

    data class SubMenu(
        @JvmField
        @Comment("Crazy sub option ngl")
        var subOption: Boolean = true
    )

    companion object {
        @JvmField
        val INSTANCE: Config<TestConfig> = ConfigRegistry.register(
            XjsConfig(
                FrozenTestMain.MOD_ID,
                TestConfig::class.java,
                XjsFormat.JSON_FORMATTED
            )
        )

        @JvmStatic
        @JvmOverloads
        fun get(real: Boolean = false): TestConfig {
            if (real) return INSTANCE.instance()
            return INSTANCE.config()
        }
    }
}
