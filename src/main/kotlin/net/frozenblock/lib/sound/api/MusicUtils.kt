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
