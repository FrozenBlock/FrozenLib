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

package net.frozenblock.lib.spotting_icons.impl;

import net.frozenblock.lib.FrozenSharedConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record SpottingIconPacket(
	int entityId,
	ResourceLocation texture,
	float startFade,
	float endFade,
	ResourceLocation restrictionID
) implements CustomPacketPayload {

	public static final Type<SpottingIconPacket> PACKET_TYPE = new Type<>(
		FrozenSharedConstants.id("spotting_icon_packet")
	);
	public static final StreamCodec<FriendlyByteBuf, SpottingIconPacket> CODEC = StreamCodec.ofMember(SpottingIconPacket::write, SpottingIconPacket::new);

	public SpottingIconPacket(FriendlyByteBuf buf) {
		this(buf.readVarInt(), buf.readResourceLocation(), buf.readFloat(), buf.readFloat(), buf.readResourceLocation());
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeVarInt(this.entityId());
		buf.writeResourceLocation(this.texture());
		buf.writeFloat(this.startFade());
		buf.writeFloat(this.endFade());
		buf.writeResourceLocation(this.restrictionID());
	}

	@Override
	@NotNull
	public Type<?> type() {
		return PACKET_TYPE;
	}
}
