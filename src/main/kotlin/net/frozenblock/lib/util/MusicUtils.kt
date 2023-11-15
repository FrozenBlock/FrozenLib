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

package net.frozenblock.lib.util

import com.mojang.serialization.Codec
import net.minecraft.core.Holder
import net.minecraft.sounds.Music
import net.minecraft.sounds.SoundEvent

/**
 * @since 1.4.4
 */
data class MutableMusic(
    @JvmField var event: Holder<SoundEvent>?,
    @JvmField var minDelay: Int?,
    @JvmField var maxDelay: Int?,
    @JvmField var replaceCurrentMusic: Boolean?
) {
    companion object {
        @JvmField
        val CODEC: Codec<MutableMusic> = Music.CODEC.xmap({ music -> music.asMutable }, { mutMusic -> mutMusic.asImmutable })
    }
}

val Music.asMutable: MutableMusic
    get() = MutableMusic(
        this.event,
        this.minDelay,
        this.maxDelay,
        this.replaceCurrentMusic()
    )

val MutableMusic?.asImmutable: Music?
    get() {
        if (this == null) return null
        val event = this.event ?: return null
        val minDelay = this.minDelay ?: return null
        val maxDelay = this.maxDelay ?: return null
        val replaceCurrentMusic = this.replaceCurrentMusic ?: return null

        return Music(event, minDelay, maxDelay, replaceCurrentMusic)
    }