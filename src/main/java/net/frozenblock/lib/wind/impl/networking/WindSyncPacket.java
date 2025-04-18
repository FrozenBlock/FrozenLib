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

package net.frozenblock.lib.wind.impl.networking;

import net.frozenblock.lib.FrozenLibConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record WindSyncPacket(
	long windTime,
	long seed,
	boolean override,
	Vec3 commandWind
) implements CustomPacketPayload {

	public static final Type<WindSyncPacket> PACKET_TYPE = new Type<>(
		FrozenLibConstants.id("wind_sync_packet")
	);
	public static final StreamCodec<FriendlyByteBuf, WindSyncPacket> CODEC = StreamCodec.ofMember(WindSyncPacket::write, WindSyncPacket::create);

	public static WindSyncPacket create(@NotNull FriendlyByteBuf buf) {
        return new WindSyncPacket(
			buf.readLong(),
			buf.readLong(),
			buf.readBoolean(),
			buf.readVec3()
		);
	}

	public void write(@NotNull FriendlyByteBuf buf) {
		buf.writeLong(this.windTime());
		buf.writeLong(this.seed());
		buf.writeBoolean(this.override());
		buf.writeVec3(this.commandWind());
	}

	@Override
	@NotNull
	public Type<? extends CustomPacketPayload> type() {
		return PACKET_TYPE;
	}
}
