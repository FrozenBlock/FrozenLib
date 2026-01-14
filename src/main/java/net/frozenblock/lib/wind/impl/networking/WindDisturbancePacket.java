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

package net.frozenblock.lib.wind.impl.networking;

import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.wind.api.WindDisturbanceLogic;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public record WindDisturbancePacket(AABB affectedArea, Vec3 origin, WindDisturbanceLogic.SourceType sourceType, Identifier id, long posOrID) implements CustomPacketPayload {
	public static final Type<WindDisturbancePacket> PACKET_TYPE = new Type<>(FrozenLibConstants.id("wind_disturbance_packet"));
	public static final StreamCodec<RegistryFriendlyByteBuf, WindDisturbancePacket> CODEC = StreamCodec.ofMember(WindDisturbancePacket::write, WindDisturbancePacket::new);

	public WindDisturbancePacket(RegistryFriendlyByteBuf buf) {
		this(
			new AABB(Vec3.STREAM_CODEC.decode(buf), Vec3.STREAM_CODEC.decode(buf)),
			Vec3.STREAM_CODEC.decode(buf),
			buf.readEnum(WindDisturbanceLogic.SourceType.class),
			buf.readIdentifier(),
			buf.readLong()
		);
	}

	public void write(RegistryFriendlyByteBuf buf) {
		AABB affectedArea = this.affectedArea();
		Vec3.STREAM_CODEC.encode(buf, new Vec3(affectedArea.minX, affectedArea.minY, affectedArea.minZ));
		Vec3.STREAM_CODEC.encode(buf, new Vec3(affectedArea.maxX, affectedArea.maxY, affectedArea.maxZ));
		Vec3.STREAM_CODEC.encode(buf, this.origin);
		buf.writeEnum(this.sourceType());
		buf.writeIdentifier(this.id());
		buf.writeLong(this.posOrID());
	}

	@Override
	public Type<?> type() {
		return PACKET_TYPE;
	}
}
