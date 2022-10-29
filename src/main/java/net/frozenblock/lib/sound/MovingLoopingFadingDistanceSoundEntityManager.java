/*
 * Copyright 2022 FrozenBlock
 * This file is part of FrozenLib.
 *
 * FrozenLib is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * FrozenLib is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with FrozenLib. If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.sound;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.sound.SoundPredicate.SoundPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import org.slf4j.Logger;

public class MovingLoopingFadingDistanceSoundEntityManager {
    ArrayList<FadingDistanceSoundLoopNBT> sounds = new ArrayList<>();
    public LivingEntity entity;
    public int ticksToCheck;

    public MovingLoopingFadingDistanceSoundEntityManager(LivingEntity entity) {
        this.entity = entity;
    }

    public void load(CompoundTag nbt) {
        nbt.putInt("frozenDistanceSoundTicksToCheck", this.ticksToCheck);
        if (nbt.contains("frozenDistanceSounds", 9)) {
            this.sounds.clear();
            DataResult<List<FadingDistanceSoundLoopNBT>> var10000 = FadingDistanceSoundLoopNBT.CODEC.listOf().parse(new Dynamic<>(NbtOps.INSTANCE, nbt.getList("frozenDistanceSounds", 10)));
            Logger var10001 = FrozenMain.LOGGER4;
            Objects.requireNonNull(var10001);
            Optional<List<FadingDistanceSoundLoopNBT>> list = var10000.resultOrPartial(var10001::error);
            if (list.isPresent()) {
                List<FadingDistanceSoundLoopNBT> allSounds = list.get();
                this.sounds.addAll(allSounds);
            }
        }
    }

    public void save(CompoundTag nbt) {
        this.ticksToCheck = nbt.getInt("frozenDistanceSoundTicksToCheck");
        DataResult<Tag> var10000 = FadingDistanceSoundLoopNBT.CODEC.listOf().encodeStart(NbtOps.INSTANCE, this.sounds);
        Logger var10001 = FrozenMain.LOGGER4;
        Objects.requireNonNull(var10001);
        var10000.resultOrPartial(var10001::error).ifPresent((cursorsNbt) -> nbt.put("frozenDistanceSounds", cursorsNbt));
    }

    public void addSound(ResourceLocation soundID, ResourceLocation soundID2, SoundSource category, float volume, float pitch, ResourceLocation restrictionId, float fadeDist, float maxDist) {
        this.sounds.add(new FadingDistanceSoundLoopNBT(soundID, soundID2, category, volume, pitch, restrictionId, fadeDist, maxDist));
    }

    public ArrayList<FadingDistanceSoundLoopNBT> getSounds() {
        return this.sounds;
    }

    public void tick() {
        if (this.ticksToCheck > 0) {
            --this.ticksToCheck;
        } else {
            this.ticksToCheck = 20;
            ArrayList<FadingDistanceSoundLoopNBT> soundsToRemove = new ArrayList<>();
            for (FadingDistanceSoundLoopNBT nbt : this.getSounds()) {
                if (!SoundPredicate.getPredicate(nbt.restrictionID).test(this.entity)) {
                    soundsToRemove.add(nbt);
                }
            }
            this.sounds.removeAll(soundsToRemove);
        }
    }

    public static class FadingDistanceSoundLoopNBT {
        public final ResourceLocation soundEventID;
        public final ResourceLocation sound2EventID;
        public final String categoryOrdinal;
        public final float volume;
        public final float pitch;
        public final float fadeDist;
        public final float maxDist;
        public final ResourceLocation restrictionID;

        public static final Codec<FadingDistanceSoundLoopNBT> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                ResourceLocation.CODEC.fieldOf("soundEventID").forGetter(FadingDistanceSoundLoopNBT::getSoundEventID),
                ResourceLocation.CODEC.fieldOf("sound2EventID").forGetter(FadingDistanceSoundLoopNBT::getSound2EventID),
                Codec.STRING.fieldOf("categoryOrdinal").forGetter(FadingDistanceSoundLoopNBT::getOrdinal),
                Codec.FLOAT.fieldOf("volume").forGetter(FadingDistanceSoundLoopNBT::getVolume),
                Codec.FLOAT.fieldOf("pitch").forGetter(FadingDistanceSoundLoopNBT::getPitch),
                ResourceLocation.CODEC.fieldOf("restrictionID").forGetter(FadingDistanceSoundLoopNBT::getRestrictionID),
                Codec.FLOAT.fieldOf("fadeDist").forGetter(FadingDistanceSoundLoopNBT::getFadeDist),
                Codec.FLOAT.fieldOf("maxDist").forGetter(FadingDistanceSoundLoopNBT::getMaxDist)
        ).apply(instance, FadingDistanceSoundLoopNBT::new));

        public FadingDistanceSoundLoopNBT(ResourceLocation soundEventID, ResourceLocation sound2EventID, String ordinal, float vol, float pitch, ResourceLocation restrictionID, float fadeDist, float maxDist) {
            this.soundEventID = soundEventID;
            this.sound2EventID = sound2EventID;
            this.categoryOrdinal = ordinal;
            this.volume = vol;
            this.pitch = pitch;
            this.restrictionID = restrictionID;
            this.fadeDist = fadeDist;
            this.maxDist = maxDist;
        }

        public FadingDistanceSoundLoopNBT(ResourceLocation soundEventID, ResourceLocation sound2EventID, SoundSource category, float vol, float pitch, ResourceLocation restrictionID, float fadeDist, float maxDist) {
            this.soundEventID = soundEventID;
            this.sound2EventID = sound2EventID;
            this.categoryOrdinal = category.toString();
            this.volume = vol;
            this.pitch = pitch;
            this.restrictionID = restrictionID;
            this.fadeDist = fadeDist;
            this.maxDist = maxDist;
        }

        public ResourceLocation getSoundEventID() {
            return this.soundEventID;
        }
        public ResourceLocation getSound2EventID() {
            return this.sound2EventID;
        }
        public String getOrdinal() {
            return this.categoryOrdinal;
        }
        public float getVolume() {
            return this.volume;
        }
        public float getPitch() {
            return this.pitch;
        }
        public float getFadeDist() {
            return this.fadeDist;
        }
        public float getMaxDist() {
            return this.maxDist;
        }
        public ResourceLocation getRestrictionID() {
            return this.restrictionID;
        }
    }
}
