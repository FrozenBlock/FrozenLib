/*
 * Copyright 2024 The Quilt Project
 * Copyright 2024 FrozenBlock
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


import net.minecraft.server.network.ServerGamePacketListenerImpl;

public interface ExtendedConnection {
	void frozenLib$setModProtocol(String id, int version);
	int frozenLib$getModProtocol(String id);

	static ExtendedConnection from(ServerGamePacketListenerImpl handler) {
		return (ExtendedConnection) handler.connection;
	}
}
