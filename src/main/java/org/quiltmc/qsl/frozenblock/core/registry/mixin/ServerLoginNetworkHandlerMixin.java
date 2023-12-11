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
import net.minecraft.network.PacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.server.ServerRegistrySync;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.server.ServerRegistrySyncNetworkHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLoginPacketListenerImpl.class)
public abstract class ServerLoginNetworkHandlerMixin {

	@Shadow
	@Final
	Connection connection;

	@Shadow
	protected abstract void placeNewPlayer(ServerPlayer player);

	@Unique
	private boolean frozenLib$continueJoining = false;

	@Inject(method = "placeNewPlayer", at = @At("HEAD"), cancellable = true)
	private void applySyncHandler(ServerPlayer player, CallbackInfo ci) {
		if (!player.server.isSingleplayerOwner(player.getGameProfile()) && !this.frozenLib$continueJoining && ServerRegistrySync.shouldSync()) {
			this.connection.setListener(new ServerRegistrySyncNetworkHandler(player, this.connection, () -> {
				this.frozenLib$continueJoining = true;
				this.connection.setListener((PacketListener) this);
				this.placeNewPlayer(player);
			}));
			ci.cancel();
		}
	}
}
