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
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ForcedCooldownPacket(
	ResourceLocation cooldownGroup,
	int startTime,
	int endTime
) implements CustomPacketPayload {
	public static final Type<ForcedCooldownPacket> PACKET_TYPE = new Type<>(FrozenLibConstants.id("forced_cooldown"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ForcedCooldownPacket> CODEC = StreamCodec.ofMember(ForcedCooldownPacket::write, ForcedCooldownPacket::new);

	public ForcedCooldownPacket(@NotNull RegistryFriendlyByteBuf buf) {
		this(buf.readResourceLocation(), buf.readVarInt(), buf.readVarInt());
	}

	public void write(@NotNull RegistryFriendlyByteBuf buf) {
		buf.writeResourceLocation(this.cooldownGroup);
		buf.writeVarInt(this.startTime());
		buf.writeVarInt(this.endTime());
	}

	@Override
	@NotNull
	public Type<?> type() {
		return PACKET_TYPE;
	}
}
