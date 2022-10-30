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

public class MovingLoopingSoundEntityManager {
    ArrayList<SoundLoopNBT> sounds = new ArrayList<>();
    public LivingEntity entity;

    public MovingLoopingSoundEntityManager(LivingEntity entity) {
        this.entity = entity;
    }

    public void load(CompoundTag nbt) {
        if (nbt.contains("frozenSounds", 9)) {
            this.sounds.clear();
            DataResult<List<SoundLoopNBT>> var10000 = SoundLoopNBT.CODEC.listOf().parse(new Dynamic<>(NbtOps.INSTANCE, nbt.getList("frozenSounds", 10)));
            Logger var10001 = FrozenMain.LOGGER4;
            Objects.requireNonNull(var10001);
            Optional<List<SoundLoopNBT>> list = var10000.resultOrPartial(var10001::error);
            if (list.isPresent()) {
                List<SoundLoopNBT> allSounds = list.get();
                this.sounds.addAll(allSounds);
            }
        }
    }

    public void save(CompoundTag nbt) {
        DataResult<Tag> var10000 = SoundLoopNBT.CODEC.listOf().encodeStart(NbtOps.INSTANCE, this.sounds);
        Logger var10001 = FrozenMain.LOGGER4;
        Objects.requireNonNull(var10001);
        var10000.resultOrPartial(var10001::error).ifPresent((cursorsNbt) -> nbt.put("frozenSounds", cursorsNbt));
    }

    public void addSound(ResourceLocation soundID, SoundSource category, float volume, float pitch, ResourceLocation restrictionId) {
        this.sounds.add(new SoundLoopNBT(soundID, category, volume, pitch, restrictionId));
    }

    public ArrayList<SoundLoopNBT> getSounds() {
        return this.sounds;
    }

    public void tick() {
		if (!this.sounds.isEmpty()) {
			ArrayList<SoundLoopNBT> soundsToRemove = new ArrayList<>();
			for (SoundLoopNBT nbt : this.sounds) {
				SoundPredicate.LoopPredicate<LivingEntity> predicate = SoundPredicate.getPredicate(nbt.restrictionID);
				if (!predicate.test(this.entity)) {
					soundsToRemove.add(nbt);
					predicate.onStop(this.entity);
				}
			}
			this.sounds.removeAll(soundsToRemove);
		}
    }

    public static class SoundLoopNBT {
        public final ResourceLocation soundEventID;
        public final String categoryOrdinal;
        public final float volume;
        public final float pitch;
        public final ResourceLocation restrictionID;

        public static final Codec<SoundLoopNBT> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                ResourceLocation.CODEC.fieldOf("soundEventID").forGetter(SoundLoopNBT::getSoundEventID),
                Codec.STRING.fieldOf("categoryOrdinal").forGetter(SoundLoopNBT::getOrdinal),
                Codec.FLOAT.fieldOf("volume").forGetter(SoundLoopNBT::getVolume),
                Codec.FLOAT.fieldOf("pitch").forGetter(SoundLoopNBT::getPitch),
                ResourceLocation.CODEC.fieldOf("restrictionID").forGetter(SoundLoopNBT::getRestrictionID)
        ).apply(instance, SoundLoopNBT::new));

        public SoundLoopNBT(ResourceLocation soundEventID, String ordinal, float vol, float pitch, ResourceLocation restrictionID) {
            this.soundEventID = soundEventID;
            this.categoryOrdinal = ordinal;
            this.volume = vol;
            this.pitch = pitch;
            this.restrictionID = restrictionID;
        }

        public SoundLoopNBT(ResourceLocation soundEventID, SoundSource category, float vol, float pitch, ResourceLocation restrictionID) {
            this.soundEventID = soundEventID;
            this.categoryOrdinal = category.toString();
            this.volume = vol;
            this.pitch = pitch;
            this.restrictionID = restrictionID;
        }

        public ResourceLocation getSoundEventID() {
            return this.soundEventID;
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
        public ResourceLocation getRestrictionID() {
            return this.restrictionID;
        }
    }
}
