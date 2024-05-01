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

package org.quiltmc.qsl.frozenblock.core.registry.mixin;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.ProtocolVersions;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.server.ExtendedConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public class ConnectionMixin implements ExtendedConnection {
	@Unique
	private Object2IntMap<String> frozenLib$modProtocol = new Object2IntOpenHashMap<>();

	@Inject(method = "<init>", at = @At("TAIL"))
	private void setDefault(PacketFlow receiving, CallbackInfo ci) {
		this.frozenLib$modProtocol.defaultReturnValue(ProtocolVersions.NO_PROTOCOL);
	}

	@Override
	public void frozenLib$setModProtocol(String id, int version) {
		this.frozenLib$modProtocol.put(id, version);
	}

	@Override
	public int frozenLib$getModProtocol(String id) {
		return this.frozenLib$modProtocol.getInt(id);
	}
}
