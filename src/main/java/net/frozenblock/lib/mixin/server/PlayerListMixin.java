package net.frozenblock.lib.mixin.server;

import net.frozenblock.lib.events.api.PlayerJoinEvent;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class PlayerListMixin {

	@Shadow @Final
	private MinecraftServer server;

	@Inject(method = "placeNewPlayer", at = @At("TAIL"))
	public void placeNewPlayer(Connection netManager, ServerPlayer player, CallbackInfo info) {
		PlayerJoinEvent.onPlayerJoined(this.server, player);
	}

}
