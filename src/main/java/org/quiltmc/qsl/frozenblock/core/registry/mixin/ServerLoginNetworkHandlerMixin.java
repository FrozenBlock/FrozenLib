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
