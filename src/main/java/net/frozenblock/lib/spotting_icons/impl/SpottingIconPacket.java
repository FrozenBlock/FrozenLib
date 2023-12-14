/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.spotting_icons.impl;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.frozenblock.lib.FrozenSharedConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record SpottingIconPacket(
	int entityId,
	ResourceLocation texture,
	float startFade,
	float endFade,
	ResourceLocation restrictionID
) implements FabricPacket {

	public static final PacketType<SpottingIconPacket> PACKET_TYPE = PacketType.create(
		FrozenSharedConstants.id("spotting_icon_packet"),
		SpottingIconPacket::new
	);

	public SpottingIconPacket(FriendlyByteBuf buf) {
		this(buf.readVarInt(), buf.readResourceLocation(), buf.readFloat(), buf.readFloat(), buf.readResourceLocation());
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeVarInt(this.entityId());
		buf.writeResourceLocation(this.texture());
		buf.writeFloat(this.startFade());
		buf.writeFloat(this.endFade());
		buf.writeResourceLocation(this.restrictionID());
	}

	@Override
	public PacketType<?> getType() {
		return PACKET_TYPE;
	}
}
