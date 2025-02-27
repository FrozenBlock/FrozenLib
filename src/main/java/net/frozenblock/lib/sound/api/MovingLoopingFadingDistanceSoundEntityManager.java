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

public class MovingLoopingFadingDistanceSoundEntityManager {
    private final ArrayList<FadingDistanceSoundLoopNBT> sounds = new ArrayList<>();
    public final Entity entity;

    public MovingLoopingFadingDistanceSoundEntityManager(Entity entity) {
        this.entity = entity;
    }

    public void load(@NotNull CompoundTag nbt) {
		this.sounds.clear();
		FadingDistanceSoundLoopNBT.CODEC.listOf()
			.parse(new Dynamic<>(NbtOps.INSTANCE, nbt.getListOrEmpty("frozenlib_looping_fading_distance_sounds")))
			.resultOrPartial(FrozenLibConstants.LOGGER::error)
			.ifPresent(this.sounds::addAll);
    }

    public void save(CompoundTag nbt) {
		if (!this.sounds.isEmpty()) {
			FadingDistanceSoundLoopNBT.CODEC.listOf().encodeStart(NbtOps.INSTANCE, this.sounds)
				.resultOrPartial(FrozenLibConstants.LOGGER::error)
				.ifPresent((cursorsNbt) -> nbt.put("frozenlib_looping_fading_distance_sounds", cursorsNbt));
		}
    }

    public void addSound(
		ResourceLocation soundID,
		ResourceLocation soundID2,
		SoundSource category,
		float volume,
		float pitch,
		ResourceLocation restrictionId,
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
		if (!this.sounds.isEmpty()) {
			ArrayList<FadingDistanceSoundLoopNBT> soundsToRemove = new ArrayList<>();
			for (FadingDistanceSoundLoopNBT nbt : this.sounds) {
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
		for (FadingDistanceSoundLoopNBT nbt : this.getSounds()) {
			FrozenLibSoundPackets.createAndSendMovingRestrictionLoopingFadingDistanceSound(
				serverPlayer,
				this.entity,
				BuiltInRegistries.SOUND_EVENT.get(nbt.soundEventID()).orElseThrow(),
				BuiltInRegistries.SOUND_EVENT.get(nbt.soundEventID2()).orElseThrow(),
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
		ResourceLocation soundEventID,
		ResourceLocation soundEventID2,
		String category,
		float volume,
		float pitch,
		float fadeDist,
		float maxDist,
		ResourceLocation restrictionID,
		boolean stopOnDeath
	) {

		public static final Codec<FadingDistanceSoundLoopNBT> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
			ResourceLocation.CODEC.fieldOf("soundEventID").forGetter(FadingDistanceSoundLoopNBT::soundEventID),
			ResourceLocation.CODEC.fieldOf("sound2EventID").forGetter(FadingDistanceSoundLoopNBT::soundEventID2),
			Codec.STRING.fieldOf("categoryOrdinal").forGetter(FadingDistanceSoundLoopNBT::category),
			Codec.FLOAT.fieldOf("volume").forGetter(FadingDistanceSoundLoopNBT::volume),
			Codec.FLOAT.fieldOf("pitch").forGetter(FadingDistanceSoundLoopNBT::pitch),
			Codec.FLOAT.fieldOf("fadeDist").forGetter(FadingDistanceSoundLoopNBT::fadeDist),
			Codec.FLOAT.fieldOf("maxDist").forGetter(FadingDistanceSoundLoopNBT::maxDist),
			ResourceLocation.CODEC.fieldOf("restrictionID").forGetter(FadingDistanceSoundLoopNBT::restrictionID),
			Codec.BOOL.fieldOf("stopOnDeath").forGetter(FadingDistanceSoundLoopNBT::stopOnDeath)
		).apply(instance, FadingDistanceSoundLoopNBT::new));

		public FadingDistanceSoundLoopNBT(
			ResourceLocation soundEventID,
			ResourceLocation soundEventID2,
			@NotNull SoundSource category,
			float volume,
			float pitch,
			float fadeDist,
			float maxDist,
			ResourceLocation restrictionID,
			boolean stopOnDeath
		) {
			this(soundEventID, soundEventID2, category.toString(), volume, pitch, fadeDist, maxDist, restrictionID, stopOnDeath);
		}
	}
}
