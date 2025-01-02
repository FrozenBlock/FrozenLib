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

package net.frozenblock.lib.wind.impl.networking;

import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.wind.api.WindDisturbanceLogic;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
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

) implements CustomPacketPayload {
	public static final Type<WindDisturbancePacket> PACKET_TYPE = new Type<>(
		FrozenSharedConstants.id("wind_disturbance_packet")
	);
	public static final StreamCodec<RegistryFriendlyByteBuf, WindDisturbancePacket> CODEC = StreamCodec.ofMember(WindDisturbancePacket::write, WindDisturbancePacket::new);

	public WindDisturbancePacket(@NotNull RegistryFriendlyByteBuf buf) {
		this(
			new AABB(buf.readVec3(), buf.readVec3()),
			buf.readVec3(),
			buf.readEnum(WindDisturbanceLogic.SourceType.class),
			buf.readResourceLocation(),
			buf.readLong()
		);
	}

	public void write(@NotNull RegistryFriendlyByteBuf buf) {
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
	public Type<?> type() {
		return PACKET_TYPE;
	}
}
