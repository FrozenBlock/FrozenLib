/*
 * Copyright (C) 2024 FrozenBlock
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

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.frozenblock.lib.FrozenSharedConstants;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record LoadCapeRepoPacket(String capeRepo) implements FabricPacket {
	public static final PacketType<LoadCapeRepoPacket> PACKET_TYPE = PacketType.create(
		FrozenSharedConstants.id("load_cape_repo"), LoadCapeRepoPacket::create
	);

	@Contract("_ -> new")
	public static @NotNull LoadCapeRepoPacket create(@NotNull FriendlyByteBuf buf) {
		return new LoadCapeRepoPacket(buf.readUtf());
	}

	public void write(@NotNull FriendlyByteBuf buf) {
		buf.writeUtf(this.capeRepo());
	}

	@Override
	public PacketType<?> getType() {
		return PACKET_TYPE;
	}
}

