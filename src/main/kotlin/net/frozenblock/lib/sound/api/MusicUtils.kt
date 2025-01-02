/*
 * Copyright (C) 2024-2025 FrozenBlock
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

package net.frozenblock.lib.sound.api

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Holder
import net.minecraft.sounds.Music
import net.minecraft.sounds.SoundEvent
import java.util.*
import kotlin.jvm.optionals.getOrNull

/**
 * @since 1.4.4
 */
data class MutableMusic(
    @JvmField var event: Holder<SoundEvent>?,
    @JvmField var minDelay: Int?,
    @JvmField var maxDelay: Int?,
    @JvmField var replaceCurrentMusic: Boolean?
) {
    /**
     * @since 1.6.1
     */
    constructor(
        event: Optional<Holder<SoundEvent>>,
        minDelay: Optional<Int>,
        maxDelay: Optional<Int>,
        replaceCurrentMusic: Optional<Boolean>
    ) : this(
        event.getOrNull(),
        minDelay.getOrNull(),
        maxDelay.getOrNull(),
        replaceCurrentMusic.getOrNull()
    )

    companion object {
        @JvmField
        val CODEC: Codec<MutableMusic> = RecordCodecBuilder.create { instance ->
            instance.group(
                SoundEvent.CODEC.optionalFieldOf("sound").forGetter { Optional.ofNullable(it.event) },
                Codec.INT.optionalFieldOf("min_delay").forGetter { Optional.ofNullable(it.minDelay) },
                Codec.INT.optionalFieldOf("max_delay").forGetter { Optional.ofNullable(it.maxDelay) },
                Codec.BOOL.optionalFieldOf("replace_current_music").forGetter { Optional.ofNullable(it.replaceCurrentMusic) }
            ).apply(instance, ::MutableMusic)
        }
    }
}

inline val Music.asMutable: MutableMusic
    get() = MutableMusic(
        this.event,
        this.minDelay,
        this.maxDelay,
        this.replaceCurrentMusic()
    )

inline val MutableMusic?.asImmutable: Music?
    get() {
        val event = this?.event ?: return null
        val minDelay = this.minDelay ?: return null
        val maxDelay = this.maxDelay ?: return null
        val replaceCurrentMusic = this.replaceCurrentMusic ?: return null

        return Music(event, minDelay, maxDelay, replaceCurrentMusic)
    }
