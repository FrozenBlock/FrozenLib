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
    @Comment("This is a sound event typed entry.")
    var randomSound: TypedEntry<Holder<SoundEvent>> = TypedEntry(
        SOUND_HOLDER_TYPE,
        SoundEvents.BREEZE_WIND_CHARGE_BURST
    ),

    @JvmField
    @Comment("This is a Vec3 list typed entry.")
    var typedVecList: TypedEntry<List<Vec3>> = TypedEntry(
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
                XjsFormat.XJS_FORMATTED
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
