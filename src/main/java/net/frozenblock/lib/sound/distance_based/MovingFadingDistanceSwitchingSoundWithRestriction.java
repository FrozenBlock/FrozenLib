package net.frozenblock.lib.sound.distance_based;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.sound.SoundPredicate.SoundPredicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

@Environment(EnvType.CLIENT)
public class MovingFadingDistanceSwitchingSoundWithRestriction<T extends Entity> extends AbstractTickableSoundInstance {

    private final T entity;
    private final SoundPredicate.LoopPredicate<T> predicate;
    private final boolean isFarSound;
    private final double maxDist;
    private final double fadeDist;
    private final float maxVol;

    public MovingFadingDistanceSwitchingSoundWithRestriction(T entity, SoundEvent sound, SoundSource category, float volume, float pitch, SoundPredicate.LoopPredicate<T> predicate, double fadeDist, double maxDist, float maxVol, boolean isFarSound) {
        super(sound, category, SoundInstance.createUnseededRandom());
        this.entity = entity;
        this.looping = true;
        this.delay = 0;
        this.volume = volume;
        this.pitch = pitch;

        this.x = (float) entity.getX();
        this.y = (float) entity.getY();
        this.z = (float) entity.getZ();
        this.predicate = predicate;
        this.isFarSound = isFarSound;
        this.maxDist = maxDist;
        this.fadeDist = fadeDist;
        this.maxVol = maxVol;
    }

    @Override
    public boolean canPlaySound() {
        return !this.entity.isSilent();
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

	@Override
	public void stop() {
		this.predicate.onStop(this.entity);
		super.stop();
	}

    @Override
    public void tick() {
        Minecraft client = Minecraft.getInstance();
        if (this.entity.isRemoved()) {
            this.stop();
        } else {
            if (!this.predicate.test(this.entity)) {
                this.stop();
            } else {
                this.x = (float) this.entity.getX();
                this.y = (float) this.entity.getY();
                this.z = (float) this.entity.getZ();
                if (client.player != null) {
                    float distance = client.player.distanceTo(this.entity);
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
    }

}
