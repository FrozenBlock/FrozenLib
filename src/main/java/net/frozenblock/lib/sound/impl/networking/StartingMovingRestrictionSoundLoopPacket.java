/*
 * Copyright (C) 2024-2025 FrozenBlock
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
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.NotNull;

public record StartingMovingRestrictionSoundLoopPacket(
	int id,
	Holder<SoundEvent> startingSound,
	Holder<SoundEvent> sound,
	SoundSource category,
	float volume,
	float pitch,
	ResourceLocation predicateId,
	boolean stopOnDeath
) implements CustomPacketPayload {
	public static final Type<StartingMovingRestrictionSoundLoopPacket> PACKET_TYPE = new Type<>(
		FrozenLibConstants.id("starting_moving_restriction_looping_sound")
	);
	public static final StreamCodec<RegistryFriendlyByteBuf, StartingMovingRestrictionSoundLoopPacket> CODEC = StreamCodec.ofMember(StartingMovingRestrictionSoundLoopPacket::write, StartingMovingRestrictionSoundLoopPacket::new);

	public StartingMovingRestrictionSoundLoopPacket(@NotNull RegistryFriendlyByteBuf buf) {
		this(
			buf.readVarInt(),
			ByteBufCodecs.holderRegistry(Registries.SOUND_EVENT).decode(buf),
			ByteBufCodecs.holderRegistry(Registries.SOUND_EVENT).decode(buf),
			buf.readEnum(SoundSource.class),
			buf.readFloat(),
			buf.readFloat(),
			buf.readResourceLocation(),
			buf.readBoolean()
		);
	}

	public void write(@NotNull RegistryFriendlyByteBuf buf) {
		buf.writeVarInt(this.id);
		ByteBufCodecs.holderRegistry(Registries.SOUND_EVENT).encode(buf, this.startingSound);
		ByteBufCodecs.holderRegistry(Registries.SOUND_EVENT).encode(buf, this.sound);
		buf.writeEnum(this.category);
		buf.writeFloat(this.volume);
		buf.writeFloat(this.pitch);
		buf.writeResourceLocation(predicateId);
		buf.writeBoolean(this.stopOnDeath);
	}

	@Override
	@NotNull
	public Type<?> type() {
		return PACKET_TYPE;
	}
}
