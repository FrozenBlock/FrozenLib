/*
 * Copyright (C) 2024-2026 FrozenBlock
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
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import net.frozenblock.lib.sound.api.predicate.SoundPredicate;
import net.frozenblock.lib.sound.impl.networking.FrozenLibSoundPackets;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class MovingLoopingSoundEntityManager {
    private final ArrayList<SoundLoopData> sounds = new ArrayList<>();
    public Entity entity;

    public MovingLoopingSoundEntityManager(Entity entity) {
        this.entity = entity;
    }

    public void load(ValueInput input) {
		this.sounds.clear();
		final ValueInput.TypedInputList<SoundLoopData> list = input.listOrEmpty("frozenlib_looping_sounds", SoundLoopData.CODEC);
		for (SoundLoopData sound : list) this.sounds.add(sound);
    }

    public void save(ValueOutput output) {
		if (this.sounds.isEmpty()) return;

		final ValueOutput.TypedOutputList<SoundLoopData> list = output.list("frozenlib_looping_sounds", SoundLoopData.CODEC);
		for (SoundLoopData sound : this.sounds) list.add(sound);
    }

    public void addSound(Identifier soundID, SoundSource category, float volume, float pitch, Identifier restrictionId, boolean stopOnDeath) {
        this.sounds.add(new SoundLoopData(soundID, category, volume, pitch, restrictionId, stopOnDeath));
		SoundPredicate.getPredicate(restrictionId).onStart(this.entity);
    }

    public List<SoundLoopData> getSounds() {
        return this.sounds;
    }

    public void tick() {
		if (this.sounds.isEmpty()) return;

		final ArrayList<SoundLoopData> soundsToRemove = new ArrayList<>();
		for (SoundLoopData nbt : this.sounds) {
			final SoundPredicate.LoopPredicate<Entity> predicate = SoundPredicate.getPredicate(nbt.restrictionID);
			if (predicate.test(this.entity)) continue;

			soundsToRemove.add(nbt);
			predicate.onStop(this.entity);
		}

		this.sounds.removeAll(soundsToRemove);
    }

	public void syncWithPlayer(ServerPlayer player) {
		for (SoundLoopData nbt : this.getSounds()) {
			FrozenLibSoundPackets.createAndSendMovingRestrictionLoopingSound(
				player,
				this.entity,
				BuiltInRegistries.SOUND_EVENT.get(nbt.sound()).orElseThrow(),
				SoundSource.valueOf(SoundSource.class, nbt.category()),
				nbt.volume,
				nbt.pitch,
				nbt.restrictionID,
				nbt.stopOnDeath
			);
		}
	}

    public record SoundLoopData(Identifier sound, String category, float volume, float pitch, Identifier restrictionID, boolean stopOnDeath) {
        public static final Codec<SoundLoopData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Identifier.CODEC.fieldOf("sound").forGetter(SoundLoopData::sound),
			Codec.STRING.fieldOf("categoryOrdinal").forGetter(SoundLoopData::category),
			Codec.FLOAT.fieldOf("volume").forGetter(SoundLoopData::volume),
			Codec.FLOAT.fieldOf("pitch").forGetter(SoundLoopData::pitch),
			Identifier.CODEC.fieldOf("restrictionID").forGetter(SoundLoopData::restrictionID),
			Codec.BOOL.fieldOf("stopOnDeath").forGetter(SoundLoopData::stopOnDeath)
        ).apply(instance, SoundLoopData::new));

        public SoundLoopData(Identifier sound, SoundSource category, float volume, float pitch, Identifier restrictionID, boolean stopOnDeath) {
			this(sound, category.toString(), volume, pitch, restrictionID, stopOnDeath);
        }
    }
}
