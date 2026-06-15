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

package net.frozenblock.lib.sound.api.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.frozenblock.lib.sound.api.predicate.SoundPredicate;
import net.frozenblock.lib.sound.impl.networking.FrozenLibSoundPackets;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

public class FadingDistanceLoopingMovingSoundType extends MovingSoundType<FadingDistanceLoopingMovingSoundType.FadingDistanceSoundLoopData> {

	public FadingDistanceLoopingMovingSoundType(Identifier attachmentId) {
		super(attachmentId, FadingDistanceSoundLoopData.CODEC);
	}

	@Override
	protected void onAdd(Entity entity, FadingDistanceSoundLoopData data) {
		SoundPredicate.getPredicate(data.restrictionID()).onStart(entity);
	}

	@Override
	protected List<FadingDistanceSoundLoopData> tick(Entity entity, List<FadingDistanceSoundLoopData> sounds) {
		List<FadingDistanceSoundLoopData> result = new ArrayList<>(sounds);
		Iterator<FadingDistanceSoundLoopData> it = result.iterator();
		while (it.hasNext()) {
			FadingDistanceSoundLoopData data = it.next();
			SoundPredicate.LoopPredicate<Entity> predicate = SoundPredicate.getPredicate(data.restrictionID());
			if (!predicate.test(entity)) {
				it.remove();
				predicate.onStop(entity);
			}
		}
		return result;
	}

	@Override
	protected void syncWithPlayer(Entity entity, ServerPlayer player, List<FadingDistanceSoundLoopData> sounds) {
		for (FadingDistanceSoundLoopData data : sounds) {
			FrozenLibSoundPackets.createAndSendMovingRestrictionLoopingFadingDistanceSound(
				player,
				entity,
				BuiltInRegistries.SOUND_EVENT.get(data.closeSound()).orElseThrow(),
				BuiltInRegistries.SOUND_EVENT.get(data.farSound()).orElseThrow(),
				SoundSource.valueOf(SoundSource.class, data.category()),
				data.volume(),
				data.pitch(),
				data.restrictionID(),
				data.stopOnDeath(),
				data.fadeDist(),
				data.maxDist()
			);
		}
	}

	public record FadingDistanceSoundLoopData(
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
		public static final Codec<FadingDistanceSoundLoopData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Identifier.CODEC.fieldOf("closeSound").forGetter(FadingDistanceSoundLoopData::closeSound),
			Identifier.CODEC.fieldOf("farSound").forGetter(FadingDistanceSoundLoopData::farSound),
			Codec.STRING.fieldOf("categoryOrdinal").forGetter(FadingDistanceSoundLoopData::category),
			Codec.FLOAT.fieldOf("volume").forGetter(FadingDistanceSoundLoopData::volume),
			Codec.FLOAT.fieldOf("pitch").forGetter(FadingDistanceSoundLoopData::pitch),
			Codec.FLOAT.fieldOf("fadeDist").forGetter(FadingDistanceSoundLoopData::fadeDist),
			Codec.FLOAT.fieldOf("maxDist").forGetter(FadingDistanceSoundLoopData::maxDist),
			Identifier.CODEC.fieldOf("restrictionID").forGetter(FadingDistanceSoundLoopData::restrictionID),
			Codec.BOOL.fieldOf("stopOnDeath").forGetter(FadingDistanceSoundLoopData::stopOnDeath)
		).apply(instance, FadingDistanceSoundLoopData::new));

		public FadingDistanceSoundLoopData(
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
