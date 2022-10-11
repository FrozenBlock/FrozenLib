package net.frozenblock.lib.interfaces;

import net.frozenblock.lib.sound.MovingLoopingFadingDistanceSoundEntityManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;

public interface EntityLoopingFadingDistanceSoundInterface {

    boolean hasSyncedFadingDistanceClient();

    MovingLoopingFadingDistanceSoundEntityManager getFadingDistanceSounds();

    void addFadingDistanceSound(ResourceLocation soundID, ResourceLocation sound2ID, SoundSource category, float volume, float pitch, ResourceLocation restrictionId, float fadeDist, float maxDist);

}