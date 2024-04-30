/*
 * Copyright 2023 FrozenBlock
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
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.sound.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.sound.api.predicate.SoundPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import org.slf4j.Logger;

public class MovingLoopingSoundEntityManager {
    private final ArrayList<SoundLoopData> sounds = new ArrayList<>();
    public Entity entity;

    public MovingLoopingSoundEntityManager(Entity entity) {
        this.entity = entity;
    }

    public void load(CompoundTag nbt) {
        if (nbt.contains("frozenSounds", 9)) {
            this.sounds.clear();
            DataResult<List<SoundLoopData>> var10000 = SoundLoopData.CODEC.listOf().parse(new Dynamic<>(NbtOps.INSTANCE, nbt.getList("frozenSounds", 10)));
            Logger var10001 = FrozenSharedConstants.LOGGER4;
            Objects.requireNonNull(var10001);
            Optional<List<SoundLoopData>> list = var10000.resultOrPartial(var10001::error);
            if (list.isPresent()) {
                List<SoundLoopData> allSounds = list.get();
                this.sounds.addAll(allSounds);
            }
        }
    }

    public void save(CompoundTag nbt) {
        DataResult<Tag> var10000 = SoundLoopData.CODEC.listOf().encodeStart(NbtOps.INSTANCE, this.sounds);
        Logger var10001 = FrozenSharedConstants.LOGGER4;
        Objects.requireNonNull(var10001);
        var10000.resultOrPartial(var10001::error).ifPresent((cursorsNbt) -> nbt.put("frozenSounds", cursorsNbt));
    }

    public void addSound(ResourceLocation soundID, SoundSource category, float volume, float pitch, ResourceLocation restrictionId, boolean stopOnDeath) {
        this.sounds.add(new SoundLoopData(soundID, category, volume, pitch, restrictionId, stopOnDeath));
		SoundPredicate.getPredicate(restrictionId).onStart(this.entity);
    }

    public List<SoundLoopData> getSounds() {
        return this.sounds;
    }

    public void tick() {
		if (!this.sounds.isEmpty()) {
			ArrayList<SoundLoopData> soundsToRemove = new ArrayList<>();
			for (SoundLoopData nbt : this.sounds) {
				SoundPredicate.LoopPredicate<Entity> predicate = SoundPredicate.getPredicate(nbt.restrictionID);
				if (!predicate.test(this.entity)) {
					soundsToRemove.add(nbt);
					predicate.onStop(this.entity);
				}
			}
			this.sounds.removeAll(soundsToRemove);
		}
    }

	public void syncWithPlayer(ServerPlayer serverPlayer) {
		for (MovingLoopingSoundEntityManager.SoundLoopData nbt : this.getSounds()) {
			FrozenSoundPackets.createMovingRestrictionLoopingSound(
				serverPlayer,
				this.entity,
				BuiltInRegistries.SOUND_EVENT.getHolder(nbt.getSoundEventID()).orElseThrow(),
				SoundSource.valueOf(SoundSource.class, nbt.getOrdinal()),
				nbt.volume,
				nbt.pitch,
				nbt.restrictionID,
				nbt.stopOnDeath
			);
		}
	}

    public static class SoundLoopData {
        public final ResourceLocation soundEventID;
        public final String categoryOrdinal;
        public final float volume;
        public final float pitch;
        public final ResourceLocation restrictionID;
		public final boolean stopOnDeath;

        public static final Codec<SoundLoopData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("soundEventID").forGetter(SoundLoopData::getSoundEventID),
                Codec.STRING.fieldOf("categoryOrdinal").forGetter(SoundLoopData::getOrdinal),
                Codec.FLOAT.fieldOf("volume").forGetter(SoundLoopData::getVolume),
                Codec.FLOAT.fieldOf("pitch").forGetter(SoundLoopData::getPitch),
                ResourceLocation.CODEC.fieldOf("restrictionID").forGetter(SoundLoopData::getRestrictionID),
				Codec.BOOL.fieldOf("stopOnDeath").forGetter(SoundLoopData::getStopOnDeath)
        ).apply(instance, SoundLoopData::new));

        public SoundLoopData(ResourceLocation soundEventID, String ordinal, float vol, float pitch, ResourceLocation restrictionID, boolean stopOnDeath) {
            this.soundEventID = soundEventID;
            this.categoryOrdinal = ordinal;
            this.volume = vol;
            this.pitch = pitch;
            this.restrictionID = restrictionID;
			this.stopOnDeath = stopOnDeath;
        }

        public SoundLoopData(ResourceLocation soundEventID, SoundSource category, float vol, float pitch, ResourceLocation restrictionID, boolean stopOnDeath) {
            this.soundEventID = soundEventID;
            this.categoryOrdinal = category.toString();
            this.volume = vol;
            this.pitch = pitch;
            this.restrictionID = restrictionID;
			this.stopOnDeath = stopOnDeath;
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

		public boolean getStopOnDeath() {
			return this.stopOnDeath;
		}

    }
}
