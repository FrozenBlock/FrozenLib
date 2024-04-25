/*
 * Copyright 2023 The Quilt Project
 * Copyright 2023 FrozenBlock
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
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.sound.api.instances;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.sound.api.RestrictedSoundInstance;
import net.frozenblock.lib.sound.api.predicate.SoundPredicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

@Environment(EnvType.CLIENT)
public class RestrictedStartingSound<T extends Entity> extends RestrictedSoundInstance<T> {

	public final boolean stopOnDeath;
    public boolean hasSwitched = false;
    public final AbstractSoundInstance nextSound;

    public RestrictedStartingSound(T entity, SoundEvent startingSound, SoundSource category, float volume, float pitch, SoundPredicate.LoopPredicate<T> predicate, boolean stopOnDeath, AbstractSoundInstance nextSound) {
        super(startingSound, category, SoundInstance.createUnseededRandom(), entity, predicate);
        this.nextSound = nextSound;
        this.looping = false;
        this.delay = 0;
        this.volume = volume;
        this.pitch = pitch;

        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
		this.stopOnDeath = stopOnDeath;
    }

    public void startNextSound() {
        this.stop();
        this.hasSwitched = true;
        Minecraft.getInstance().getSoundManager().play(this.nextSound);
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
            if (this.stopOnDeath && this.entity.isRemoved()) {
                this.stop();
            } else {
                if (!this.test()) {
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

                    this.x = this.entity.getX();
                    this.y = this.entity.getY();
                    this.z = this.entity.getZ();
                }
            }
        }
    }
}
