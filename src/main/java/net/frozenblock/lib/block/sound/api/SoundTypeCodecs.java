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

package net.frozenblock.lib.block.sound.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.block.sound.impl.overwrite.HolderSetBlockSoundTypeOverwrite;
import net.minecraft.core.Holder;
import net.minecraft.core.IdMap;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;

@UtilityClass
public class SoundTypeCodecs {
	public static final Codec<SoundType> SOUND_TYPE = RecordCodecBuilder.create(instance ->
		instance.group(
			Codec.FLOAT.fieldOf("volume").forGetter(SoundType::getVolume),
			Codec.FLOAT.fieldOf("pitch").forGetter(SoundType::getPitch),
			BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("break_sound").forGetter(SoundType::getBreakSound),
			BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("step_sound").forGetter(SoundType::getStepSound),
			BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("place_sound").forGetter(SoundType::getPlaceSound),
			BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("hit_sound").forGetter(SoundType::getHitSound),
			BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("fall_sound").forGetter(SoundType::getFallSound)
		).apply(instance, SoundType::new)
	);
	public static final StreamCodec<RegistryFriendlyByteBuf, SoundEvent> SOUND_EVENT_STREAM_CODEC = holderValue(
		Registries.SOUND_EVENT,
		SoundEvent.DIRECT_STREAM_CODEC
	);
	public static final StreamCodec<RegistryFriendlyByteBuf, SoundType> SOUND_TYPE_STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.FLOAT, SoundType::getVolume,
		ByteBufCodecs.FLOAT, SoundType::getPitch,
		SOUND_EVENT_STREAM_CODEC, SoundType::getBreakSound,
		SOUND_EVENT_STREAM_CODEC, SoundType::getStepSound,
		SOUND_EVENT_STREAM_CODEC, SoundType::getPlaceSound,
		SOUND_EVENT_STREAM_CODEC, SoundType::getHitSound,
		SOUND_EVENT_STREAM_CODEC, SoundType::getFallSound,
		SoundType::new
	);
	public static final Codec<HolderSetBlockSoundTypeOverwrite> HOLDER_SET_BLOCK_SOUND_TYPE_OVERWRITE_CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
			RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("blocks").forGetter(HolderSetBlockSoundTypeOverwrite::getValue),
			SOUND_TYPE.fieldOf("sound_type").forGetter(HolderSetBlockSoundTypeOverwrite::getSoundType)
		).apply(instance, (tag, soundType) -> new HolderSetBlockSoundTypeOverwrite(tag, soundType, () -> true))
	);

	static <T> StreamCodec<RegistryFriendlyByteBuf, T> holderValue(
		final ResourceKey<? extends Registry<T>> registryKey, final StreamCodec<? super RegistryFriendlyByteBuf, T> directCodec
	) {
		return new StreamCodec<>() {
			private static final int DIRECT_HOLDER_ID = 0;

			private IdMap<Holder<T>> getRegistryOrThrow(final RegistryFriendlyByteBuf input) {
				return input.registryAccess().lookupOrThrow(registryKey).asHolderIdMap();
			}

			@Override
			public T decode(final RegistryFriendlyByteBuf input) {
				int id = VarInt.read(input);
				return id == DIRECT_HOLDER_ID ? directCodec.decode(input) : this.getRegistryOrThrow(input).byIdOrThrow(id - 1).value();
			}

			@Override
			public void encode(final RegistryFriendlyByteBuf output, final T value) {
				var lookup = output.registryAccess().lookupOrThrow(registryKey);
				var holder = lookup.wrapAsHolder(value);
				switch (holder.kind()) {
					case REFERENCE:
						int id = this.getRegistryOrThrow(output).getIdOrThrow(holder);
						VarInt.write(output, id + 1);
						break;
					case DIRECT:
						VarInt.write(output, DIRECT_HOLDER_ID);
						directCodec.encode(output, holder.value());
				}
			}
		};
	}
}
