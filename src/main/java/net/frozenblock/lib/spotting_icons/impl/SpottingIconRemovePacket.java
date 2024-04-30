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

package net.frozenblock.lib.spotting_icons.impl;

import net.frozenblock.lib.FrozenSharedConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record SpottingIconRemovePacket(
	int entityId
) implements CustomPacketPayload {

	public static final Type<SpottingIconRemovePacket> PACKET_TYPE = CustomPacketPayload.createType(
		FrozenSharedConstants.string("spotting_icon_remove_packet")
	);
	public static final StreamCodec<FriendlyByteBuf, SpottingIconRemovePacket> CODEC = ByteBufCodecs.VAR_INT
		.map(SpottingIconRemovePacket::new, SpottingIconRemovePacket::entityId)
		.cast();

	public void write(FriendlyByteBuf buf) {
		buf.writeVarInt(this.entityId());
	}

	@Override
	@NotNull
	public Type<?> type() {
		return PACKET_TYPE;
	}
}
