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
	public Connection connection;

	@Shadow
	protected abstract void placeNewPlayer(ServerPlayer player);

	@Unique
	private boolean quilt$continueJoining = false;

	@Inject(method = "placeNewPlayer", at = @At("HEAD"), cancellable = true)
	private void quilt$applySyncHandler(ServerPlayer player, CallbackInfo ci) {
		if (!player.server.isSingleplayerOwner(player.getGameProfile()) && !this.quilt$continueJoining && ServerRegistrySync.shouldSync()) {
			this.connection.setListener(new ServerRegistrySyncNetworkHandler(player, this.connection, () -> {
				this.quilt$continueJoining = true;
				this.connection.setListener((PacketListener) this);
				this.placeNewPlayer(player);
			}));
			ci.cancel();
		}
	}
}
