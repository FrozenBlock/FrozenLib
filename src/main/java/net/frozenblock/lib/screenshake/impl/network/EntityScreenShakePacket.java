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

package net.frozenblock.lib.screenshake.impl.network;

import net.frozenblock.lib.FrozenLibConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record EntityScreenShakePacket(int entityId, float intensity, int duration, int falloffStart, float maxDistance, int ticks) implements CustomPacketPayload {
	public static final Type<EntityScreenShakePacket> PACKET_TYPE = new Type<>(FrozenLibConstants.id("screen_shake_entity"));
	public static final StreamCodec<FriendlyByteBuf, EntityScreenShakePacket> CODEC = StreamCodec.ofMember(EntityScreenShakePacket::write, EntityScreenShakePacket::new);

	public EntityScreenShakePacket(FriendlyByteBuf buf) {
		this(buf.readVarInt(), buf.readFloat(), buf.readInt(), buf.readInt(), buf.readFloat(), buf.readInt());
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeVarInt(this.entityId());
		buf.writeFloat(this.intensity());
		buf.writeInt(this.duration());
		buf.writeInt(this.falloffStart());
		buf.writeFloat(this.maxDistance());
		buf.writeInt(this.ticks());
	}

	@Override
	public Type<?> type() {
		return PACKET_TYPE;
	}
}
