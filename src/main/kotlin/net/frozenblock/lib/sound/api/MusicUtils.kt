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

package net.frozenblock.lib.sound.api

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Holder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.sounds.Music
import net.minecraft.sounds.SoundEvent

/**
 * @since 1.4.4
 */
data class MutableMusic(
    @JvmField var sound: Holder<SoundEvent>,
    @JvmField var minDelay: Int,
    @JvmField var maxDelay: Int,
    @JvmField var replaceCurrentMusic: Boolean
) {

    companion object {
        @JvmField
        val CODEC: Codec<MutableMusic> = RecordCodecBuilder.create { instance ->
            instance.group(
                SoundEvent.CODEC.fieldOf("sound").forGetter(MutableMusic::sound),
                Codec.INT.fieldOf("min_delay").forGetter(MutableMusic::minDelay),
                Codec.INT.fieldOf("max_delay").forGetter(MutableMusic::maxDelay),
                Codec.BOOL.fieldOf("replace_current_music").forGetter(MutableMusic::replaceCurrentMusic)
            ).apply(instance, ::MutableMusic)
        }

        @JvmField
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, MutableMusic> = StreamCodec.composite(
            SoundEvent.STREAM_CODEC, MutableMusic::sound,
            ByteBufCodecs.VAR_INT, MutableMusic::minDelay,
            ByteBufCodecs.VAR_INT, MutableMusic::maxDelay,
            ByteBufCodecs.BOOL, MutableMusic::replaceCurrentMusic,
            ::MutableMusic
        )
    }
}

inline val Music.asMutable: MutableMusic
    get() = MutableMusic(
        this.sound,
        this.minDelay,
        this.maxDelay,
        this.replaceCurrentMusic()
    )

inline val MutableMusic?.asImmutable: Music?
    get() {
        val sound = this?.sound ?: return null
        val minDelay = this.minDelay ?: return null
        val maxDelay = this.maxDelay ?: return null
        val replaceCurrentMusic = this.replaceCurrentMusic ?: return null

        return Music(sound, minDelay, maxDelay, replaceCurrentMusic)
    }
