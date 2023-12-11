/*
 * Copyright 2023 The Quilt Project
 * Copyright 2023 FrozenBlock
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

import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.server.DelayedPacketsHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class PlayerListMixin {

	@Inject(method = "placeNewPlayer", at = @At("TAIL"))
	private void sendSync(Connection netManager, ServerPlayer player, CallbackInfo ci) {
		var delayedList = ((DelayedPacketsHolder) player).frozenLib$getPacketList();

		if (delayedList != null) {
			for (var packet : delayedList) {
				packet.handle(player.connection);
			}
		}

		((DelayedPacketsHolder) player).frozenLib$setPacketList(null);
	}
}
