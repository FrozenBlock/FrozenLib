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

package net.frozenblock.lib.item.impl.network;

import net.frozenblock.lib.FrozenLibConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public record CooldownChangePacket(
	Item item,
	int additional
) implements CustomPacketPayload {
	public static final Type<CooldownChangePacket> PACKET_TYPE = new Type<>(FrozenLibConstants.id("cooldown_change_packet"));
	public static final StreamCodec<RegistryFriendlyByteBuf, CooldownChangePacket> CODEC = StreamCodec.ofMember(CooldownChangePacket::write, CooldownChangePacket::new);

	public CooldownChangePacket(RegistryFriendlyByteBuf buf) {
		this(ByteBufCodecs.registry(Registries.ITEM).decode(buf), buf.readVarInt());
	}

	public void write(RegistryFriendlyByteBuf buf) {
		ByteBufCodecs.registry(Registries.ITEM).encode(buf, this.item());
		buf.writeVarInt(this.additional());
	}

	@Override
	@NotNull
	public Type<?> type() {
		return PACKET_TYPE;
	}
}
