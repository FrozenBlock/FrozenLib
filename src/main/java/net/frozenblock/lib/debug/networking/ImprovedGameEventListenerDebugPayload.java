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

package net.frozenblock.lib.debug.networking;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.frozenblock.lib.FrozenSharedConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.PositionSourceType;

public record ImprovedGameEventListenerDebugPayload(PositionSource listenerPos, int listenerRange) implements FabricPacket {
	public static final PacketType<ImprovedGameEventListenerDebugPayload> PACKET_TYPE = PacketType.create(
		FrozenSharedConstants.id("debug_game_event_listener"), ImprovedGameEventListenerDebugPayload::new
	);

	public ImprovedGameEventListenerDebugPayload(FriendlyByteBuf buf) {
		this(PositionSourceType.fromNetwork(buf), buf.readVarInt());
	}

	public ImprovedGameEventListenerDebugPayload(PositionSource listenerPos, int listenerRange) {
		this.listenerPos = listenerPos;
		this.listenerRange = listenerRange;
	}

	public void write(FriendlyByteBuf buf) {
		PositionSourceType.toNetwork(this.listenerPos, buf);
		buf.writeVarInt(this.listenerRange);
	}
	public PositionSource listenerPos() {
		return this.listenerPos;
	}

	public int listenerRange() {
		return this.listenerRange;
	}

	@Override
	public PacketType<?> getType() {
		return PACKET_TYPE;
	}
}
