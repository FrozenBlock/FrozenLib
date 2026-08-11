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

public record MovingFadingDistanceSwitchingRestrictionSoundPacket(
	int entityId,
	Holder<SoundEvent> closeSound,
	Holder<SoundEvent> farSound,
	SoundSource source,
	float volume,
	float pitch,
	float fadeDist,
	float maxDist,
	Identifier predicateId,
	boolean stopOnDeath,
	boolean looping
) implements CustomPacketPayload {
	public static final Type<MovingFadingDistanceSwitchingRestrictionSoundPacket> PACKET_TYPE = new Type<>(FrozenLibConstants.id("moving_fading_restriction_sound"));
	public static final StreamCodec<RegistryFriendlyByteBuf, MovingFadingDistanceSwitchingRestrictionSoundPacket> CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT, MovingFadingDistanceSwitchingRestrictionSoundPacket::entityId,
		SoundEvent.STREAM_CODEC, MovingFadingDistanceSwitchingRestrictionSoundPacket::closeSound,
		SoundEvent.STREAM_CODEC, MovingFadingDistanceSwitchingRestrictionSoundPacket::farSound,
		FrozenLibSoundPackets.SOUND_SOURCE_STREAM_CODEC, MovingFadingDistanceSwitchingRestrictionSoundPacket::source,
		ByteBufCodecs.FLOAT, MovingFadingDistanceSwitchingRestrictionSoundPacket::volume,
		ByteBufCodecs.FLOAT, MovingFadingDistanceSwitchingRestrictionSoundPacket::pitch,
		ByteBufCodecs.FLOAT, MovingFadingDistanceSwitchingRestrictionSoundPacket::fadeDist,
		ByteBufCodecs.FLOAT, MovingFadingDistanceSwitchingRestrictionSoundPacket::maxDist,
		Identifier.STREAM_CODEC, MovingFadingDistanceSwitchingRestrictionSoundPacket::predicateId,
		ByteBufCodecs.BOOL, MovingFadingDistanceSwitchingRestrictionSoundPacket::stopOnDeath,
		ByteBufCodecs.BOOL, MovingFadingDistanceSwitchingRestrictionSoundPacket::looping,
		MovingFadingDistanceSwitchingRestrictionSoundPacket::new
	);

	@Override
	public Type<?> type() {
		return PACKET_TYPE;
	}
}
