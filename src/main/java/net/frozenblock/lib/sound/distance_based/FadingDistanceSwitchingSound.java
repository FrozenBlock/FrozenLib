package net.frozenblock.lib.sound.distance_based;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

@Environment(EnvType.CLIENT)
public class FadingDistanceSwitchingSound extends AbstractTickableSoundInstance {

    private final boolean isFarSound;
    private final double maxDist;
    private final double fadeDist;
    private final float maxVol;

    public FadingDistanceSwitchingSound(SoundEvent sound, SoundSource category, float volume, float pitch, double fadeDist, double maxDist, float maxVol, boolean isFarSound, double x, double y, double z) {
        super(sound, category, SoundInstance.createUnseededRandom());
        this.delay = 0;
        this.volume = volume;
        this.pitch = pitch;

        this.isFarSound = isFarSound;
        this.maxDist = maxDist;
        this.fadeDist = fadeDist;
        this.maxVol = maxVol;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void tick() {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null) {
            float distance = (float) Math.sqrt(client.player.distanceToSqr(this.x, this.y, this.z));
            if (distance < this.fadeDist) {
                this.volume = !this.isFarSound ? this.maxVol : 0.001F;
            } else if (distance > this.maxDist) {
                this.volume = this.isFarSound ? this.maxVol : 0.001F;
            } else {
                //Gets lower as you move farther
                float fadeProgress = (float) ((this.maxDist - distance) / (this.maxDist - this.fadeDist));
                this.volume = this.isFarSound ? (1F - fadeProgress) * this.maxVol : fadeProgress * this.maxVol;
            }
        }
    }

}
