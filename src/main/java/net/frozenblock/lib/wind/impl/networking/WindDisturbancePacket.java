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
import net.frozenblock.lib.wind.api.WindDisturbanceLogic;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record WindDisturbancePacket(
	AABB affectedArea,
	Vec3 origin,
	WindDisturbanceLogic.SourceType disturbanceSourceType,
	ResourceLocation id,
	long posOrID

) implements FabricPacket {
	public static final PacketType<WindDisturbancePacket> PACKET_TYPE = PacketType.create(
		FrozenSharedConstants.id("wind_disturbance_packet"),
		WindDisturbancePacket::new
	);

	public WindDisturbancePacket(@NotNull FriendlyByteBuf buf) {
		this(
			new AABB(buf.readVec3(), buf.readVec3()),
			buf.readVec3(),
			buf.readEnum(WindDisturbanceLogic.SourceType.class),
			buf.readResourceLocation(),
			buf.readLong()
		);
	}

	public void write(@NotNull FriendlyByteBuf buf) {
		AABB affectedArea = this.affectedArea();
		buf.writeVec3(new Vec3(affectedArea.minX, affectedArea.minY, affectedArea.minZ));
		buf.writeVec3(new Vec3(affectedArea.maxX, affectedArea.maxY, affectedArea.maxZ));
		buf.writeVec3(this.origin());
		buf.writeEnum(this.disturbanceSourceType());
		buf.writeResourceLocation(this.id());
		buf.writeLong(this.posOrID());
	}

	@Override
	@NotNull
	public PacketType<?> getType() {
		return PACKET_TYPE;
	}
}
