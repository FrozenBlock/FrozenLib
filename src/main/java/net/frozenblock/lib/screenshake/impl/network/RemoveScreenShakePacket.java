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
import org.jetbrains.annotations.NotNull;

public record RemoveScreenShakePacket() implements CustomPacketPayload {
	public static final Type<RemoveScreenShakePacket> PACKET_TYPE = new Type<>(
		FrozenLibConstants.id("remove_screen_shakes")
	);
	public static final StreamCodec<FriendlyByteBuf, RemoveScreenShakePacket> CODEC = StreamCodec.ofMember(RemoveScreenShakePacket::write, RemoveScreenShakePacket::new);

	public RemoveScreenShakePacket(FriendlyByteBuf buf) {
		this();
	}

	public void write(FriendlyByteBuf buf) {
	}

	@Override
	@NotNull
	public Type<?> type() {
		return PACKET_TYPE;
	}
}
