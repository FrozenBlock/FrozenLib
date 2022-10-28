package net.frozenblock.lib.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.sound.SoundPredicate.SoundPredicate;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

@Environment(EnvType.CLIENT)
public class MovingSoundLoopWithRestriction extends AbstractTickableSoundInstance {

    private final Entity entity;
    private final SoundPredicate.LoopPredicate<?> predicate;

    public MovingSoundLoopWithRestriction(Entity entity, SoundEvent sound, SoundSource category, float volume, float pitch, SoundPredicate.LoopPredicate<?> predicate) {
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
        if (this.entity.isRemoved()) {
            this.stop();
        } else {
            if (!this.predicate.test(this.entity)) {
                this.stop();
            } else {
                this.x = (float) this.entity.getX();
                this.y = (float) this.entity.getY();
                this.z = (float) this.entity.getZ();
            }
        }
    }

}
