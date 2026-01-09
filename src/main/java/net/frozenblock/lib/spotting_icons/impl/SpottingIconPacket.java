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

package net.frozenblock.lib.spotting_icons.impl;

import net.frozenblock.lib.FrozenLibConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public record SpottingIconPacket(int entityId, Identifier texture, float startFade, float endFade, Identifier restrictionID) implements CustomPacketPayload {
	public static final Type<SpottingIconPacket> PACKET_TYPE = new Type<>(FrozenLibConstants.id("spotting_icon_packet"));
	public static final StreamCodec<FriendlyByteBuf, SpottingIconPacket> CODEC = StreamCodec.ofMember(SpottingIconPacket::write, SpottingIconPacket::new);

	public SpottingIconPacket(FriendlyByteBuf buf) {
		this(buf.readVarInt(), buf.readIdentifier(), buf.readFloat(), buf.readFloat(), buf.readIdentifier());
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeVarInt(this.entityId());
		buf.writeIdentifier(this.texture());
		buf.writeFloat(this.startFade());
		buf.writeFloat(this.endFade());
		buf.writeIdentifier(this.restrictionID());
	}

	@Override
	public Type<?> type() {
		return PACKET_TYPE;
	}
}
