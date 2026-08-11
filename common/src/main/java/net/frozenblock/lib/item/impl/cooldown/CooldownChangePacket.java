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

package net.frozenblock.lib.item.impl.cooldown;

import net.frozenblock.lib.FrozenLibConstants;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record CooldownChangePacket(Identifier cooldownGroup, int additional) implements CustomPacketPayload {
	public static final Type<CooldownChangePacket> PACKET_TYPE = new Type<>(FrozenLibConstants.id("cooldown_change"));
	public static final StreamCodec<RegistryFriendlyByteBuf, CooldownChangePacket> CODEC = StreamCodec.composite(
		Identifier.STREAM_CODEC, CooldownChangePacket::cooldownGroup,
		ByteBufCodecs.VAR_INT, CooldownChangePacket::additional,
		CooldownChangePacket::new
	);

	@Override
	public Type<?> type() {
		return PACKET_TYPE;
	}
}
