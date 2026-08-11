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

package net.frozenblock.lib.sound.impl.networking;

import net.frozenblock.lib.FrozenLibConstants;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public record MovingRestrictionSoundPacket(
	int entityId,
	Holder<SoundEvent> sound,
	SoundSource source,
	float volume,
	float pitch,
	Identifier predicateId,
	boolean stopOnDeath,
	boolean looping
) implements CustomPacketPayload {
	public static final Type<MovingRestrictionSoundPacket> PACKET_TYPE = new Type<>(FrozenLibConstants.id("moving_restriction_sound"));
	public static final StreamCodec<RegistryFriendlyByteBuf, MovingRestrictionSoundPacket> CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT, MovingRestrictionSoundPacket::entityId,
		SoundEvent.STREAM_CODEC, MovingRestrictionSoundPacket::sound,
		FrozenLibSoundPackets.SOUND_SOURCE_STREAM_CODEC, MovingRestrictionSoundPacket::source,
		ByteBufCodecs.FLOAT, MovingRestrictionSoundPacket::volume,
		ByteBufCodecs.FLOAT, MovingRestrictionSoundPacket::pitch,
		Identifier.STREAM_CODEC, MovingRestrictionSoundPacket::predicateId,
		ByteBufCodecs.BOOL, MovingRestrictionSoundPacket::stopOnDeath,
		ByteBufCodecs.BOOL, MovingRestrictionSoundPacket::looping,
		MovingRestrictionSoundPacket::new
	);

	@Override
	public Type<?> type() {
		return PACKET_TYPE;
	}
}
