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

package net.frozenblock.lib.wind.impl.networking;

import net.frozenblock.lib.FrozenLibConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;

public record WindAccessPacket(Vec3 accessPos) implements CustomPacketPayload {
	public static final Type<WindAccessPacket> PACKET_TYPE = new Type<>(FrozenLibConstants.id("wind_access"));
	public static final StreamCodec<FriendlyByteBuf, WindAccessPacket> STREAM_CODEC = StreamCodec.ofMember(WindAccessPacket::write, WindAccessPacket::create);

	@Contract("_ -> new")
	public static WindAccessPacket create(FriendlyByteBuf buf) {
        return new WindAccessPacket(Vec3.STREAM_CODEC.decode(buf));
	}

	public void write(FriendlyByteBuf buf) {
		Vec3.STREAM_CODEC.encode(buf, this.accessPos());
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return PACKET_TYPE;
	}
}
