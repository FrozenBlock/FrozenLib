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

import net.frozenblock.lib.networking.api.ClientNetworkingHelper;
import net.frozenblock.lib.platform.api.ClientOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

@ClientOnly
public final class ClientNetworkingHelperImpl {

	public static <P extends CustomPacketPayload> void registerGlobalClientReceiver(
		CustomPacketPayload.Type<P> type,
		ClientNetworkingHelper.ClientPayloadHandler<P> handler
	) {
		NetworkingHelperImpl.CLIENT_HANDLERS.put(type, (payload, context) ->
			handler.receive((P) payload, Minecraft.getInstance(), (LocalPlayer) context.player())
		);
	}

	public static void sendToServer(CustomPacketPayload payload) {
		Minecraft.getInstance().getConnection().send(payload);
	}

	public static <P extends CustomPacketPayload> void registerGlobalClientConfigReceiver(
		CustomPacketPayload.Type<P> type,
		ClientNetworkingHelper.ClientConfigPayloadHandler<P> handler
	) {
		NetworkingHelperImpl.CLIENT_CONFIG_HANDLERS.put(type, (payload, context) ->
			handler.receive((P) payload, Minecraft.getInstance(), NetworkingHelperImpl.wrapNeoSender(context.listener()))
		);
	}
}
