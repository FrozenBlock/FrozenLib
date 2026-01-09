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
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
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

	public static final Codec<HolderSetBlockSoundTypeOverwrite> HOLDER_SET_BLOCK_SOUND_TYPE_OVERWRITE_CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
			RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("blocks").forGetter(HolderSetBlockSoundTypeOverwrite::getValue),
			SOUND_TYPE.fieldOf("sound_type").forGetter(HolderSetBlockSoundTypeOverwrite::getSoundType)
		).apply(instance, (tag, soundType) -> new HolderSetBlockSoundTypeOverwrite(tag, soundType, () -> true))
	);
}
