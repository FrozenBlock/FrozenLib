/*
 * Copyright (C) 2024-2025 FrozenBlock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.sound.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.sound.api.predicate.SoundPredicate;
import net.frozenblock.lib.sound.impl.networking.FrozenLibSoundPackets;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class MovingLoopingSoundEntityManager {
    private final ArrayList<SoundLoopData> sounds = new ArrayList<>();
    public Entity entity;

    public MovingLoopingSoundEntityManager(Entity entity) {
        this.entity = entity;
    }

    public void load(@NotNull CompoundTag nbt) {
		this.sounds.clear();
		SoundLoopData.CODEC.listOf()
			.parse(new Dynamic<>(NbtOps.INSTANCE, nbt.getListOrEmpty("frozenlib_looping_sounds")))
			.resultOrPartial(FrozenLibConstants.LOGGER::error)
			.ifPresent(this.sounds::addAll);
    }

    public void save(CompoundTag nbt) {
		if (!this.sounds.isEmpty()) {
			SoundLoopData.CODEC.listOf()
				.encodeStart(NbtOps.INSTANCE, this.sounds)
				.resultOrPartial(FrozenLibConstants.LOGGER::error)
				.ifPresent((sounds) -> nbt.put("frozenlib_looping_sounds", sounds));
		}
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
		for (SoundLoopData nbt : this.getSounds()) {
			FrozenLibSoundPackets.createAndSendMovingRestrictionLoopingSound(
				serverPlayer,
				this.entity,
				BuiltInRegistries.SOUND_EVENT.get(nbt.soundEventID()).orElseThrow(),
				SoundSource.valueOf(SoundSource.class, nbt.category()),
				nbt.volume,
				nbt.pitch,
				nbt.restrictionID,
				nbt.stopOnDeath
			);
		}
	}

    public record SoundLoopData(
		ResourceLocation soundEventID, String category, float volume, float pitch, ResourceLocation restrictionID, boolean stopOnDeath
	) {
        public static final Codec<SoundLoopData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("soundEventID").forGetter(SoundLoopData::soundEventID),
                Codec.STRING.fieldOf("categoryOrdinal").forGetter(SoundLoopData::category),
                Codec.FLOAT.fieldOf("volume").forGetter(SoundLoopData::volume),
                Codec.FLOAT.fieldOf("pitch").forGetter(SoundLoopData::pitch),
                ResourceLocation.CODEC.fieldOf("restrictionID").forGetter(SoundLoopData::restrictionID),
				Codec.BOOL.fieldOf("stopOnDeath").forGetter(SoundLoopData::stopOnDeath)
        ).apply(instance, SoundLoopData::new));

        public SoundLoopData(
			ResourceLocation soundEventID, @NotNull SoundSource category, float volume, float pitch, ResourceLocation restrictionID, boolean stopOnDeath
		) {
			this(soundEventID, category.toString(), volume, pitch, restrictionID, stopOnDeath);
        }
    }
}
