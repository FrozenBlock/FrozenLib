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

package net.frozenblock.lib.wind.impl.networking;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.frozenblock.lib.FrozenSharedConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record WindAccessPacket(Vec3 accessPos) implements FabricPacket {
	public static final PacketType<WindAccessPacket> PACKET_TYPE = PacketType.create(
		FrozenSharedConstants.id("wind_access"), WindAccessPacket::create
	);
	@Contract("_ -> new")
	public static @NotNull WindAccessPacket create(@NotNull FriendlyByteBuf buf) {
        return new WindAccessPacket(
			buf.readVec3()
		);
	}

	public void write(@NotNull FriendlyByteBuf buf) {
		buf.writeVec3(this.accessPos());
	}

	@Override
	public PacketType<?> getType() {
		return PACKET_TYPE;
	}
}
