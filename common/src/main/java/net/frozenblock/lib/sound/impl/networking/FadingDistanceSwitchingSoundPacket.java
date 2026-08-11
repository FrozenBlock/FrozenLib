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
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

public record FadingDistanceSwitchingSoundPacket(
	Vec3 pos,
	Holder<SoundEvent> closeSound,
	Holder<SoundEvent> farSound,
	SoundSource source,
	float volume,
	float pitch,
	float fadeDist,
	float maxDist
) implements CustomPacketPayload {
	public static final Type<FadingDistanceSwitchingSoundPacket> PACKET_TYPE = new Type<>(FrozenLibConstants.id("fading_distance_sound"));
	public static final StreamCodec<RegistryFriendlyByteBuf, FadingDistanceSwitchingSoundPacket> CODEC = StreamCodec.composite(
		Vec3.STREAM_CODEC, FadingDistanceSwitchingSoundPacket::pos,
		SoundEvent.STREAM_CODEC, FadingDistanceSwitchingSoundPacket::closeSound,
		SoundEvent.STREAM_CODEC, FadingDistanceSwitchingSoundPacket::farSound,
		FrozenLibSoundPackets.SOUND_SOURCE_STREAM_CODEC, FadingDistanceSwitchingSoundPacket::source,
		ByteBufCodecs.FLOAT, FadingDistanceSwitchingSoundPacket::volume,
		ByteBufCodecs.FLOAT, FadingDistanceSwitchingSoundPacket::pitch,
		ByteBufCodecs.FLOAT, FadingDistanceSwitchingSoundPacket::fadeDist,
		ByteBufCodecs.FLOAT, FadingDistanceSwitchingSoundPacket::maxDist,
		FadingDistanceSwitchingSoundPacket::new
	);

	@Override
	public Type<?> type() {
		return PACKET_TYPE;
	}
}
