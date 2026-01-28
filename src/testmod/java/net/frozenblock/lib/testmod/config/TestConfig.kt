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

package net.frozenblock.lib.testmod.config

import blue.endless.jankson.Comment
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import net.frozenblock.lib.FrozenLibConstants
import net.frozenblock.lib.config.v2.config.ConfigData
import net.frozenblock.lib.config.v2.config.ConfigSettings
import net.frozenblock.lib.config.v2.entry.ConfigEntry
import net.frozenblock.lib.config.v2.entry.EntryType
import net.frozenblock.lib.config.v2.entry.property.VisibilityPredicate
import net.minecraft.core.Holder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.phys.Vec3

private val SOUND_ENTRY_TYPE: EntryType<Holder<SoundEvent>> = EntryType.create(
    BuiltInRegistries.SOUND_EVENT.holderByNameCodec(), ByteBufCodecs.holderRegistry(Registries.SOUND_EVENT)
)

object TestConfig {
    @JvmField
    val CONFIG: ConfigData<*> = ConfigData.createAndRegister(
        FrozenLibConstants.config("testmod"),
        ConfigSettings.DJS
    )
    @JvmField
    @Comment("This is a boolean value.")
    var testToggle: ConfigEntry<Boolean> = CONFIG.entry("testToggle", EntryType.BOOL, true)

    @JvmField
    @Comment("This is an integer value.")
    var testInt: ConfigEntry<Int> = CONFIG.entry("testInt", EntryType.INT, 69)

    @JvmField
    @Comment("This is a long value.")
    var testLong: ConfigEntry<Long> = CONFIG.entry("testLong", EntryType.LONG, 69420L)

    @JvmField
    @Comment("This is a float value.")
    var testFloat: ConfigEntry<Float> = CONFIG.entry("testFloat", EntryType.FLOAT, 69.420f)

    @JvmField
    @Comment("This is a double value.")
    var testDouble: ConfigEntry<Double> = CONFIG.entry("testDouble", EntryType.DOUBLE, 69.4206942069420)

    @JvmField
    @Comment("This is an integer list typed entry.")
    var testIntList: ConfigEntry<List<Int>> = CONFIG.entry("testIntList", EntryType.INT.asList(), listOf(45))

    @JvmField
    @Comment("This is a sound event typed entry.")
    var randomSound: ConfigEntry<Holder<SoundEvent>> = CONFIG.entry(
        "randomSound",
        SOUND_ENTRY_TYPE,
        SoundEvents.BREEZE_WIND_CHARGE_BURST
    )

    @JvmField
    @Comment("This is a Vec3 list typed entry.")
    var typedVecList: ConfigEntry<List<Vec3>> = CONFIG.entry(
        "typedVecList",
        EntryType.VEC3.asList(),
        listOf(
            Vec3(0.0, 0.0, 0.0),
            Vec3(1.0, 1.0, 1.0)
        )
    )

    @JvmField
    @Comment("This is a list of doubles")
    var doubleList: ConfigEntry<List<Double>> = CONFIG.entry("doubleList", EntryType.DOUBLE.asList(), listOf(1.0, 2.0, 3.0, Math.PI))

    @JvmField
    @Comment("This should not be seen unless config serialization is bugged")
    var mysteriousUnknownToggle: ConfigEntry<Boolean> = CONFIG.entryBuilder("mysteriousUnknownToggle", EntryType.BOOL, false)
        .visibilityPredicate(VisibilityPredicate.of { false })
        .build()

    @JvmField
    @Comment("Sub menu!")
    var subMenu: ConfigEntry<SubMenu> = CONFIG.entry("subMenu", SubMenu.ENTRY_TYPE, SubMenu())

    data class SubMenu(
        @JvmField
        @Comment("Crazy sub option ngl")
        var subOption: Boolean = true
    ) {
        companion object {
            val CODEC: Codec<SubMenu> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.BOOL.fieldOf("subOption").forGetter { it.subOption }
                ).apply(instance, ::SubMenu)
            }
            val STREAM_CODEC: StreamCodec<ByteBuf, SubMenu> = StreamCodec.composite(
                ByteBufCodecs.BOOL, SubMenu::subOption, ::SubMenu
            )
            val ENTRY_TYPE: EntryType<SubMenu> = EntryType.create(CODEC, STREAM_CODEC)
        }
    }
}
