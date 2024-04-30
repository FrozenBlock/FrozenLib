/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.sound.api.networking;

import net.frozenblock.lib.FrozenSharedConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record FadingDistanceSwitchingSoundPacket(
	Vec3 pos,
	Holder<SoundEvent> closeSound,
	Holder<SoundEvent> farSound,
	SoundSource category,
	float volume,
	float pitch,
	float fadeDist,
	float maxDist
) implements CustomPacketPayload {

	public static final Type<FadingDistanceSwitchingSoundPacket> PACKET_TYPE = CustomPacketPayload.createType(
		FrozenSharedConstants.string("fading_distance_sound_packet")
	);
	public static final StreamCodec<RegistryFriendlyByteBuf, FadingDistanceSwitchingSoundPacket> CODEC = StreamCodec.ofMember(FadingDistanceSwitchingSoundPacket::write, FadingDistanceSwitchingSoundPacket::new);

	public FadingDistanceSwitchingSoundPacket(@NotNull RegistryFriendlyByteBuf buf) {
		this(
			buf.readVec3(),
			ByteBufCodecs.holderRegistry(Registries.SOUND_EVENT).decode(buf),
			ByteBufCodecs.holderRegistry(Registries.SOUND_EVENT).decode(buf),
			buf.readEnum(SoundSource.class),
			buf.readFloat(),
			buf.readFloat(),
			buf.readFloat(),
			buf.readFloat()
		);
	}

	public void write(@NotNull RegistryFriendlyByteBuf buf) {
		buf.writeVec3(this.pos());
		ByteBufCodecs.holderRegistry(Registries.SOUND_EVENT).encode(buf, this.closeSound());
		ByteBufCodecs.holderRegistry(Registries.SOUND_EVENT).encode(buf, this.farSound());
		buf.writeEnum(this.category());
		buf.writeFloat(this.volume());
		buf.writeFloat(this.pitch());
		buf.writeFloat(this.fadeDist());
		buf.writeFloat(this.maxDist());
	}

	@Override
	@NotNull
	public Type<?> type() {
		return PACKET_TYPE;
	}
}
