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

package net.frozenblock.lib.worldgen.structure.impl.status.networking;

import java.util.List;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.worldgen.structure.impl.status.PlayerStructureStatus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record PlayerStructureStatusPacket(List<PlayerStructureStatus> statuses) implements CustomPacketPayload {
	public static final StreamCodec<RegistryFriendlyByteBuf, PlayerStructureStatusPacket> CODEC = StreamCodec.ofMember(PlayerStructureStatusPacket::write, PlayerStructureStatusPacket::new);

	public static final Type<PlayerStructureStatusPacket> PACKET_TYPE = new Type<>(
		FrozenLibConstants.id("player_structure_status")
	);
	public PlayerStructureStatusPacket(RegistryFriendlyByteBuf buf) {
		this(buf.readList(PlayerStructureStatus.STREAM_CODEC));
	}

	public void write(RegistryFriendlyByteBuf buf) {
		buf.writeCollection(this.statuses, PlayerStructureStatus.STREAM_CODEC);
	}

	@Override
	public Type<?> type() {
		return PACKET_TYPE;
	}
}
