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

package net.frozenblock.lib.particle.options;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.particle.FrozenLibParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ControlledNoteParticleOptions(int note) implements ParticleOptions {
	public static final MapCodec<ControlledNoteParticleOptions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.INT.fieldOf("note").forGetter(ControlledNoteParticleOptions::note)
	).apply(instance, ControlledNoteParticleOptions::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ControlledNoteParticleOptions> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT, ControlledNoteParticleOptions::note,
		ControlledNoteParticleOptions::new
	);

	@Override
	public ParticleType<?> getType() {
		return FrozenLibParticleTypes.WIND_SMALL;
	}
}
