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

package org.quiltmc.qsl.frozenblock.core.registry.mixin.client;

import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ServerData;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.mod_protocol.ModProtocolContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Environment(EnvType.CLIENT)
@Mixin(ServerData.class)
public class ServerDataMixin implements ModProtocolContainer {
	@Unique
	private Map<String, IntList> frozenLib$modProtocol;

	@Override
	public void frozenLib$setModProtocol(Map<String, IntList> map) {
		this.frozenLib$modProtocol = map;
	}

	@Override
	public Map<String, IntList> frozenLib$getModProtocol() {
		return this.frozenLib$modProtocol;
	}
}
