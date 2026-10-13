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
import java.util.function.Supplier;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.frozenblock.lib.sound.api.predicate.SoundPredicate;
import net.frozenblock.lib.sound.impl.networking.FrozenLibSoundPackets;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

public class MovingLoopingSoundType extends MovingSoundType<MovingLoopingSoundType.Data> {

	public MovingLoopingSoundType(Identifier attachmentId) {
		super(attachmentId, Data.CODEC);
	}

	@Override
	protected void onAdd(Entity entity, Data data) {
		((SoundPredicate) data.predicateType().value().get()).onStart(entity);
	}

	@Override
	protected List<Data> tick(Entity entity, List<Data> sounds) {
		final List<Data> result = new ArrayList<>(sounds);
		final Iterator<Data> iterator = result.iterator();
		while (iterator.hasNext()) {
			final Data data = iterator.next();
			final SoundPredicate predicate = data.predicateType().value().get();
			if (!predicate.test(entity)) {
				iterator.remove();
				predicate.onStop(entity);
			}
		}
		return result;
	}

	@Override
	protected void syncWithPlayer(Entity entity, ServerPlayer player, List<Data> sounds) {
		for (Data data : sounds) {
			FrozenLibSoundPackets.createAndSendMovingRestrictionLoopingSound(
				player,
				entity,
				data.sound(),
				SoundSource.valueOf(SoundSource.class, data.category()),
				data.volume(),
				data.pitch(),
				data.predicateType(),
				data.stopOnDeath()
			);
		}
	}

	public record Data(
		Holder<SoundEvent> sound,
		String category,
		float volume,
		float pitch,
		Holder<Supplier<SoundPredicate<?>>> predicateType,
		boolean stopOnDeath
	) {
		public static final Codec<Data> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			SoundEvent.CODEC.fieldOf("sound").forGetter(Data::sound),
			Codec.STRING.fieldOf("categoryOrdinal").forGetter(Data::category),
			Codec.FLOAT.fieldOf("volume").forGetter(Data::volume),
			Codec.FLOAT.fieldOf("pitch").forGetter(Data::pitch),
			RegistryCodecs.holder(FrozenLibRegistries.SOUND_PREDICATE_TYPE).fieldOf("sound_predicate_type").forGetter(Data::predicateType),
			Codec.BOOL.fieldOf("stopOnDeath").forGetter(Data::stopOnDeath)
		).apply(instance, Data::new));

		public Data(Holder<SoundEvent> sound, SoundSource category, float volume, float pitch, Holder<Supplier<SoundPredicate<?>>> predicateType, boolean stopOnDeath) {
			this(sound, category.toString(), volume, pitch, predicateType, stopOnDeath);
		}
	}
}
