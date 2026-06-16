/*
 * Copyright (C) 2026 FrozenBlock
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

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.wind.disturbance.WindDisturbance;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public record WindDisturbanceSyncPacket(
	int entityId,
	WindDisturbance<?> disturbance,
	boolean add
) implements CustomPacketPayload {
	public static final Type<WindDisturbanceSyncPacket> TYPE = new Type<>(FrozenLibConstants.id("wind_disturbance_sync"));
	public static final StreamCodec<RegistryFriendlyByteBuf, WindDisturbanceSyncPacket> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT,
		WindDisturbanceSyncPacket::entityId,
		WindDisturbance.STREAM_CODEC,
		WindDisturbanceSyncPacket::disturbance,
		ByteBufCodecs.BOOL,
		WindDisturbanceSyncPacket::add,
		WindDisturbanceSyncPacket::new
	);

	public static void sendToTracking(ServerLevel level, Entity entity, WindDisturbance<?> disturbance, boolean add) {
		var packet = new WindDisturbanceSyncPacket(entity.getId(), disturbance, add);
		for (ServerPlayer player : PlayerLookup.tracking(entity)) {
			ServerPlayNetworking.send(player, packet);
		}
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
