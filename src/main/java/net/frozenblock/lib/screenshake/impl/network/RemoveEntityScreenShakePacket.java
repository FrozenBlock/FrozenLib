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

package net.frozenblock.lib.screenshake.impl.network;

import net.frozenblock.lib.FrozenSharedConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record RemoveEntityScreenShakePacket(
	int entityId
) implements CustomPacketPayload {

	public static final Type<RemoveEntityScreenShakePacket> PACKET_TYPE = CustomPacketPayload.createType(
		FrozenSharedConstants.string("remove_entity_screen_shakes_packet")
	);
	public static final StreamCodec<FriendlyByteBuf, RemoveEntityScreenShakePacket> CODEC = ByteBufCodecs.VAR_INT.map(RemoveEntityScreenShakePacket::new, RemoveEntityScreenShakePacket::entityId).cast();

	@Override
	@NotNull
	public Type<?> type() {
		return PACKET_TYPE;
	}
}
