/*
 * Copyright 2024-2025 The Quilt Project
 * Copyright 2024-2025 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.quiltmc.qsl.frozenblock.core.registry.impl.sync.server;

import java.util.function.Consumer;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.frozenblock.lib.FrozenLibConstants;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.ClientPackets;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.ProtocolVersions;

public class QuiltSyncTask implements ConfigurationTask {
	public static final Type TYPE = new Type(FrozenLibConstants.string("registry_sync"));
	private final ServerConfigurationPacketListenerImpl packetHandler;
	private final ExtendedConnection extendedConnection;
	private Consumer<Packet<?>> sender;
	private int syncVersion = ProtocolVersions.NO_PROTOCOL;

	public QuiltSyncTask(ServerConfigurationPacketListenerImpl packetHandler, Connection connection) {
		this.packetHandler = packetHandler;
		this.extendedConnection = (ExtendedConnection) connection;
	}

	@Override
	public void start(Consumer<Packet<?>> sender) {
		ServerRegistrySync.sendHelloPacket(ServerConfigurationNetworking.getSender(this.packetHandler));
	}

	@Override
	public Type type() {
		return TYPE;
	}

	private void sendSyncPackets(PacketSender sender) {
		ServerRegistrySync.sendSyncPackets(sender, this.syncVersion);
	}

	public void handleHandshake(ClientPackets.Handshake handshake) {
		this.syncVersion = handshake.version();
		this.sendSyncPackets(ServerConfigurationNetworking.getSender(this.packetHandler));
	}

	public void handleModProtocol(ClientPackets.ModProtocol protocol, PacketSender sender) {
		protocol.protocols().forEach(this.extendedConnection::frozenLib$setModProtocol);
	}

	public void handleEnd(ClientPackets.End end) {
		if (this.syncVersion == ProtocolVersions.NO_PROTOCOL && ServerRegistrySync.requiresSync()) {
			this.packetHandler.disconnect(ServerRegistrySync.noRegistrySyncMessage);
		} else {
			this.packetHandler.completeTask(TYPE);
		}
	}
}
