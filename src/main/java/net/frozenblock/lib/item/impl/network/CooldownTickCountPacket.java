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

package net.frozenblock.lib.item.impl.network;

import net.frozenblock.lib.FrozenLibConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record CooldownTickCountPacket(int count) implements CustomPacketPayload {
	public static final Type<CooldownTickCountPacket> PACKET_TYPE = new Type<>(FrozenLibConstants.id("cooldown_tick_count_packet"));
	public static final StreamCodec<FriendlyByteBuf, CooldownTickCountPacket> CODEC = ByteBufCodecs.VAR_INT.map(CooldownTickCountPacket::new, CooldownTickCountPacket::count).cast();

	@Override
	public Type<?> type() {
		return PACKET_TYPE;
	}
}
