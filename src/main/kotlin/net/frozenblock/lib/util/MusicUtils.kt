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