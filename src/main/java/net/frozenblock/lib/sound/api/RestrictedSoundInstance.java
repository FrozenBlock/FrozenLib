package net.frozenblock.lib.sound.api;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public abstract class RestrictedSoundInstance extends AbstractTickableSoundInstance {
	protected RestrictedSoundInstance(SoundEvent soundEvent, SoundSource soundSource, RandomSource randomSource) {
		super(soundEvent, soundSource, randomSource);
	}
}
