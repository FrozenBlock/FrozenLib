package net.frozenblock.lib.sound;

import com.mojang.blaze3d.audio.Library;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

@Environment(EnvType.CLIENT)
public class StartingSoundInstance extends AbstractTickableSoundInstance {

    public final Entity entity;
    public final RegisterMovingSoundRestrictions.LoopPredicate<?> predicate;
    public final SoundEvent loopingSound;
    public boolean hasSwitched = false;

    public StartingSoundInstance(Entity entity, StartingSound startingSound, SoundEvent loopingSound, SoundSource category, float volume, float pitch, RegisterMovingSoundRestrictions.LoopPredicate<?> predicate) {
        super(startingSound, category, SoundInstance.createUnseededRandom());
        this.loopingSound = loopingSound;
        this.entity = entity;
        this.looping = false;
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
        if (!hasSwitched) {
            this.looping = true;
            this.location = this.loopingSound.getLocation();
            Minecraft.getInstance().getSoundManager().queueTickingSound(this);
        } else {
            super.stop();
        }
    }

    @Override
    public void tick() {
        var soundManager = Minecraft.getInstance().getSoundManager();
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
