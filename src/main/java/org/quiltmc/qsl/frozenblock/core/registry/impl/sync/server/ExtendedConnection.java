/*
 * Copyright 2023 FrozenBlock
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
