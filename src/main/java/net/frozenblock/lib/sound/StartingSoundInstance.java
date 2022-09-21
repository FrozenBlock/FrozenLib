package net.frozenblock.lib.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

@Environment(EnvType.CLIENT)
public class StartingSoundInstance extends AbstractTickableSoundInstance {

    public final Entity entity;
    public final RegisterMovingSoundRestrictions.LoopPredicate<?> predicate;
    public final SoundEvent loopingSound;
    public final StartingSound startingSound;
    public boolean hasSwitched = false;
    public final AbstractSoundInstance nextSound;

    public StartingSoundInstance(Entity entity, StartingSound startingSound, SoundEvent loopingSound, SoundSource category, float volume, float pitch, RegisterMovingSoundRestrictions.LoopPredicate<?> predicate, AbstractSoundInstance nextSound) {
        super(startingSound, category, SoundInstance.createUnseededRandom());
        this.startingSound = startingSound;
        this.nextSound = nextSound;
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

    public void startNextSound() {
        this.stop();
        this.hasSwitched = true;
        Minecraft.getInstance().getSoundManager().play(this.nextSound);
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
    public boolean isStopped() {
        if (this.hasSwitched) {
            return true;
        }
        return super.isStopped();
    }

    @Override
    public void tick() {
        if (!this.isStopped()) {
            if (this.entity.isRemoved()) {
                this.stop();
            } else {
                if (!this.predicate.test(this.entity)) {
                    this.stop();
                } else {
                    var soundManager = Minecraft.getInstance().getSoundManager();
                    var soundEngine = soundManager.soundEngine;
                    var channelHandle = soundEngine.instanceToChannel.get(this);
                    if (channelHandle != null) {
                        channelHandle.execute(source -> {
                            if (!source.playing()) {
                                this.startNextSound();
                            }
                        });
                    }

                    this.x = (float) this.entity.getX();
                    this.y = (float) this.entity.getY();
                    this.z = (float) this.entity.getZ();
                }
            }
        }
    }
}
