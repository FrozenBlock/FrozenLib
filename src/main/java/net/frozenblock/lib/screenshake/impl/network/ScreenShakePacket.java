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

import net.frozenblock.lib.FrozenSharedConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record ScreenShakePacket(
	float intensity,
	int duration,
	int falloffStart,
	Vec3 pos,
	float maxDistance,
	int ticks
) implements CustomPacketPayload {

	public static final Type<ScreenShakePacket> PACKET_TYPE = new Type<>(
		FrozenSharedConstants.id("screen_shake_packet")
	);
	public static final StreamCodec<FriendlyByteBuf, ScreenShakePacket> CODEC = StreamCodec.ofMember(ScreenShakePacket::write, ScreenShakePacket::new);

	public ScreenShakePacket(FriendlyByteBuf buf) {
		this(
			buf.readFloat(),
			buf.readInt(),
			buf.readInt(),
			buf.readVec3(),
			buf.readFloat(),
			buf.readInt()
		);
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeFloat(this.intensity());
		buf.writeInt(this.duration());
		buf.writeInt(this.falloffStart());
		buf.writeVec3(this.pos());
		buf.writeFloat(this.maxDistance());
		buf.writeInt(this.ticks());
	}

	@Override
	@NotNull
	public Type<?> type() {
		return PACKET_TYPE;
	}
}
