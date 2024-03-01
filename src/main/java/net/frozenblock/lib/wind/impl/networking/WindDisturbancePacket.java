/*
 * Copyright 2023-2024 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.wind.impl.networking;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.wind.api.ClientWindManager;
import net.frozenblock.lib.wind.api.WindDisturbance;
import net.frozenblock.lib.wind.api.WindDisturbanceLogic;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
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
	public static final Type<WindDisturbancePacket> PACKET_TYPE = CustomPacketPayload.createType(
		FrozenSharedConstants.string("wind_disturbance_packet")
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

	@Environment(EnvType.CLIENT)
	public static void receive(@NotNull WindDisturbancePacket packet, @NotNull ClientPlayNetworking.Context ctx) {
		ClientLevel level = ctx.player().clientLevel;
		long posOrID = packet.posOrID();
		Optional<WindDisturbanceLogic> disturbanceLogic = WindDisturbanceLogic.getWindDisturbanceLogic(packet.id());
		if (disturbanceLogic.isPresent()) {
			WindDisturbanceLogic.SourceType sourceType = packet.disturbanceSourceType();
			Optional source = Optional.empty();
			if (sourceType == WindDisturbanceLogic.SourceType.ENTITY) {
				source = Optional.ofNullable(level.getEntity((int) posOrID));
			} else if (sourceType == WindDisturbanceLogic.SourceType.BLOCK_ENTITY) {
				source = Optional.ofNullable(level.getBlockEntity(BlockPos.of(posOrID)));
			}

			ClientWindManager.addWindDisturbance(
				new WindDisturbance(
					source,
					packet.origin(),
					packet.affectedArea(),
					disturbanceLogic.get()
				)
			);
		}
	}

	@Override
	@NotNull
	public Type<?> type() {
		return PACKET_TYPE;
	}
}
