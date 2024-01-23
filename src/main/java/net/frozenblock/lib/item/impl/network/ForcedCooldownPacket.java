/*
 * Copyright 2023-2024 FrozenBlock
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

package net.frozenblock.lib.item.impl.network;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.frozenblock.lib.FrozenSharedConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;

public record ForcedCooldownPacket(
	Item item,
	int startTime,
	int endTime
) implements FabricPacket {

	public static final PacketType<ForcedCooldownPacket> PACKET_TYPE = PacketType.create(
		FrozenSharedConstants.id("forced_cooldown_packet"),
		ForcedCooldownPacket::new
	);

	public ForcedCooldownPacket(FriendlyByteBuf buf) {
		this(buf.readById(BuiltInRegistries.ITEM), buf.readVarInt(), buf.readVarInt());
	}


	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeId(BuiltInRegistries.ITEM, this.item());
		buf.writeVarInt(this.startTime());
		buf.writeVarInt(this.endTime());
	}

	@Override
	public PacketType<?> getType() {
		return PACKET_TYPE;
	}
}
