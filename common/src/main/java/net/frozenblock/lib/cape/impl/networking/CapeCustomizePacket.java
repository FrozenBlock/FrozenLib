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

package net.frozenblock.lib.cape.impl.networking;

import java.util.Optional;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.cape.api.CapeUtil;
import net.frozenblock.lib.cape.impl.Cape;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public record CapeCustomizePacket(Optional<Cape> cape) implements CustomPacketPayload {
	public static final Type<CapeCustomizePacket> TYPE = new Type<>(FrozenLibConstants.id("customize_cape"));
	public static final StreamCodec<RegistryFriendlyByteBuf, CapeCustomizePacket> CODEC = StreamCodec.composite(
		Cape.NETWORK_CODEC, CapeCustomizePacket::cape,
		CapeCustomizePacket::new
	);

	public static CapeCustomizePacket createDisable() {
		return new CapeCustomizePacket(Optional.empty());
	}

	public static CapeCustomizePacket create(Identifier capeID) {
		return new CapeCustomizePacket(CapeUtil.getCape(capeID).filter(cape -> !cape.dummy()));
	}

	@Override
	public Type<?> type() {
		return TYPE;
	}

	public static void handle(CapeCustomizePacket packet, ServerPlayer player) {
		final boolean empty = packet.cape().isEmpty() || packet.cape.get().dummy() || !CapeUtil.canPlayerUserCape(player.getUUID(), packet.cape().get());
		Cape.ATTACHMENT_TYPE.set(player, empty ? Optional.empty() : packet.cape());
	}
}
