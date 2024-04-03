/*
 * Copyright 2023-2024 FrozenBlock
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
package net.frozenblock.lib.testmod.config

import blue.endless.jankson.Comment
import com.mojang.datafixers.kinds.App
import com.mojang.datafixers.util.Function6
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.frozenblock.lib.config.api.instance.Config
import net.frozenblock.lib.config.api.instance.xjs.XjsConfig
import net.frozenblock.lib.config.api.instance.xjs.XjsFormat
import net.frozenblock.lib.config.api.registry.ConfigRegistry
import net.frozenblock.lib.testmod.FrozenTestMain
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.phys.Vec3
import java.util.function.Function

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
    var randomSound: SoundEvent = SoundEvents.BEE_LOOP,

    @JvmField
    @Comment("This is a Vec3 list typed entry.")
    var typedVecList: List<Vec3> = listOf(Vec3(0.0, 0.0, 0.0), Vec3(1.0, 1.0, 1.0)),

    @JvmField
    @Comment("This is a list of doubles")
    var doubleList: List<Double> = listOf(1.0, 2.0, 3.0, Math.PI),

    @JvmField
    @Comment("Sub menu!")
    var subMenu: SubMenu = SubMenu(),
) {

    data class SubMenu(
        @JvmField
        @Comment("Crazy sub option ngl")
        var subOption: Boolean = true
    ) {
        companion object {
            @JvmField
            val CODEC: Codec<SubMenu> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.BOOL.fieldOf("subOption").forGetter { it.subOption }
                ).apply(instance, ::SubMenu)
            }
        }
    }

    companion object {
        @JvmField
        val INSTANCE: Config<TestConfig> = ConfigRegistry.register(
            XjsConfig(
                FrozenTestMain.MOD_ID,
                TestConfig::class.java,
                XjsFormat.XJS_FORMATTED,
                RecordCodecBuilder.create { instance ->
                    instance.group(
                        Codec.BOOL.fieldOf("testToggle").forGetter { it.testToggle },
                        Codec.INT.fieldOf("testInt").forGetter { it.testInt },
                        Codec.LONG.fieldOf("testLong").forGetter {it.testLong },
                        Codec.FLOAT.fieldOf("testFloat").forGetter { it.testFloat },
                        Codec.DOUBLE.fieldOf("testDouble").forGetter { it.testDouble },
                        Codec.INT.listOf().fieldOf("testIntList").forGetter { it.testIntList },
                        SoundEvent.DIRECT_CODEC.fieldOf("randomSound").forGetter { it.randomSound },
                        Vec3.CODEC.listOf().fieldOf("typedVecList").forGetter { it.typedVecList },
                        Codec.DOUBLE.listOf().fieldOf("doubleList").forGetter { it.doubleList },
                        SubMenu.CODEC.fieldOf("subMenu").forGetter { it.subMenu },
                    ).apply(instance, ::TestConfig)
                }
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
