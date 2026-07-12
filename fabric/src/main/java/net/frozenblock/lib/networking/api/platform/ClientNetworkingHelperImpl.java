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

package net.frozenblock.lib.networking.api.platform;

import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.frozenblock.lib.networking.api.ClientNetworkingHelper;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

@ClientOnly
public final class ClientNetworkingHelperImpl {

	public static <P extends CustomPacketPayload> void registerGlobalClientReceiver(
		CustomPacketPayload.Type<P> type,
		ClientNetworkingHelper.ClientPayloadHandler<P> handler
	) {
		ClientPlayNetworking.registerGlobalReceiver(type, (packet, ctx) ->
			handler.receive(packet, ctx.client(), ctx.player())
		);
	}

	public static void sendToServer(CustomPacketPayload payload) {
		ClientPlayNetworking.send(payload);
	}

	public static <P extends CustomPacketPayload> void registerGlobalClientConfigReceiver(
		CustomPacketPayload.Type<P> type,
		ClientNetworkingHelper.ClientConfigPayloadHandler<P> handler
	) {
		ClientConfigurationNetworking.registerGlobalReceiver(type, (payload, ctx) ->
			handler.receive(payload, ctx.client(), NetworkingHelperImpl.wrapFabricSender(ctx.responseSender()))
		);
	}
}
