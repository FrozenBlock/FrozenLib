package org.quiltmc.qsl.frozenblock.core.registry.impl.sync.server;

import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;

public interface ExtendedConnection {
	void frozenLib$setModProtocol(String id, int version);
	int frozenLib$getModProtocol(String id);

	static ExtendedConnection from(ServerConfigurationPacketListener handler) {
		return (ExtendedConnection) ((ServerConfigurationPacketListenerImpl) handler).connection;
	}
}
