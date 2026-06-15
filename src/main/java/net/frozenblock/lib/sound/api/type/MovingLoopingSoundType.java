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

public class MovingLoopingSoundType extends MovingSoundType<MovingLoopingSoundType.SoundLoopData> {

	public MovingLoopingSoundType(Identifier attachmentId) {
		super(attachmentId, SoundLoopData.CODEC);
	}

	@Override
	protected void onAdd(Entity entity, SoundLoopData data) {
		SoundPredicate.getPredicate(data.restrictionID()).onStart(entity);
	}

	@Override
	protected List<SoundLoopData> tick(Entity entity, List<SoundLoopData> sounds) {
		List<SoundLoopData> result = new ArrayList<>(sounds);
		Iterator<SoundLoopData> it = result.iterator();
		while (it.hasNext()) {
			SoundLoopData data = it.next();
			SoundPredicate.LoopPredicate<Entity> predicate = SoundPredicate.getPredicate(data.restrictionID());
			if (!predicate.test(entity)) {
				it.remove();
				predicate.onStop(entity);
			}
		}
		return result;
	}

	@Override
	protected void syncWithPlayer(Entity entity, ServerPlayer player, List<SoundLoopData> sounds) {
		for (SoundLoopData data : sounds) {
			FrozenLibSoundPackets.createAndSendMovingRestrictionLoopingSound(
				player,
				entity,
				BuiltInRegistries.SOUND_EVENT.get(data.sound()).orElseThrow(),
				SoundSource.valueOf(SoundSource.class, data.category()),
				data.volume(),
				data.pitch(),
				data.restrictionID(),
				data.stopOnDeath()
			);
		}
	}

	public record SoundLoopData(
		Identifier sound,
		String category,
		float volume,
		float pitch,
		Identifier restrictionID,
		boolean stopOnDeath
	) {
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
