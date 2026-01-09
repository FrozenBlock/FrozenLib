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
import net.frozenblock.lib.sound.api.predicate.SoundPredicate;
import net.frozenblock.lib.sound.impl.networking.FrozenLibSoundPackets;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class MovingLoopingFadingDistanceSoundEntityManager {
    private final ArrayList<FadingDistanceSoundLoopNBT> sounds = new ArrayList<>();
    public final Entity entity;

    public MovingLoopingFadingDistanceSoundEntityManager(Entity entity) {
        this.entity = entity;
    }

    public void load(ValueInput input) {
		this.sounds.clear();
		final ValueInput.TypedInputList<FadingDistanceSoundLoopNBT> list = input.listOrEmpty("frozenlib_looping_fading_distance_sounds", FadingDistanceSoundLoopNBT.CODEC);
		for (FadingDistanceSoundLoopNBT sound : list) this.sounds.add(sound);
    }

    public void save(ValueOutput nbt) {
		if (this.sounds.isEmpty()) return;

		final ValueOutput.TypedOutputList<FadingDistanceSoundLoopNBT> list = nbt.list("frozenlib_looping_fading_distance_sounds", FadingDistanceSoundLoopNBT.CODEC);
		for (FadingDistanceSoundLoopNBT sound : this.sounds) list.add(sound);
    }

    public void addSound(
		Identifier soundID,
		Identifier soundID2,
		SoundSource category,
		float volume,
		float pitch,
		Identifier restrictionId,
		boolean stopOnDeath,
		float fadeDist,
		float maxDist
	) {
        this.sounds.add(new FadingDistanceSoundLoopNBT(soundID, soundID2, category, volume, pitch, fadeDist, maxDist, restrictionId, stopOnDeath));
		SoundPredicate.getPredicate(restrictionId).onStart(this.entity);
    }

    public ArrayList<FadingDistanceSoundLoopNBT> getSounds() {
        return this.sounds;
    }

	public void tick() {
		if (this.sounds.isEmpty()) return;

		final ArrayList<FadingDistanceSoundLoopNBT> soundsToRemove = new ArrayList<>();
		for (FadingDistanceSoundLoopNBT nbt : this.sounds) {
			final SoundPredicate.LoopPredicate<Entity> predicate = SoundPredicate.getPredicate(nbt.restrictionID);
			if (predicate.test(this.entity)) continue;

			soundsToRemove.add(nbt);
			predicate.onStop(this.entity);
		}

		this.sounds.removeAll(soundsToRemove);
	}

	public void syncWithPlayer(ServerPlayer player) {
		for (FadingDistanceSoundLoopNBT nbt : this.getSounds()) {
			FrozenLibSoundPackets.createAndSendMovingRestrictionLoopingFadingDistanceSound(
				player,
				this.entity,
				BuiltInRegistries.SOUND_EVENT.get(nbt.closeSound()).orElseThrow(),
				BuiltInRegistries.SOUND_EVENT.get(nbt.farSound()).orElseThrow(),
				SoundSource.valueOf(SoundSource.class, nbt.category),
				nbt.volume,
				nbt.pitch,
				nbt.restrictionID,
				nbt.stopOnDeath,
				nbt.fadeDist,
				nbt.maxDist
			);
		}
	}

    public record FadingDistanceSoundLoopNBT(
		Identifier closeSound,
		Identifier farSound,
		String category,
		float volume,
		float pitch,
		float fadeDist,
		float maxDist,
		Identifier restrictionID,
		boolean stopOnDeath
	) {
		public static final Codec<FadingDistanceSoundLoopNBT> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
			Identifier.CODEC.fieldOf("closeSound").forGetter(FadingDistanceSoundLoopNBT::closeSound),
			Identifier.CODEC.fieldOf("farSound").forGetter(FadingDistanceSoundLoopNBT::farSound),
			Codec.STRING.fieldOf("categoryOrdinal").forGetter(FadingDistanceSoundLoopNBT::category),
			Codec.FLOAT.fieldOf("volume").forGetter(FadingDistanceSoundLoopNBT::volume),
			Codec.FLOAT.fieldOf("pitch").forGetter(FadingDistanceSoundLoopNBT::pitch),
			Codec.FLOAT.fieldOf("fadeDist").forGetter(FadingDistanceSoundLoopNBT::fadeDist),
			Codec.FLOAT.fieldOf("maxDist").forGetter(FadingDistanceSoundLoopNBT::maxDist),
			Identifier.CODEC.fieldOf("restrictionID").forGetter(FadingDistanceSoundLoopNBT::restrictionID),
			Codec.BOOL.fieldOf("stopOnDeath").forGetter(FadingDistanceSoundLoopNBT::stopOnDeath)
		).apply(instance, FadingDistanceSoundLoopNBT::new));

		public FadingDistanceSoundLoopNBT(
			Identifier closeSound,
			Identifier farSound,
			SoundSource category,
			float volume,
			float pitch,
			float fadeDist,
			float maxDist,
			Identifier restrictionID,
			boolean stopOnDeath
		) {
			this(closeSound, farSound, category.toString(), volume, pitch, fadeDist, maxDist, restrictionID, stopOnDeath);
		}
	}
}
