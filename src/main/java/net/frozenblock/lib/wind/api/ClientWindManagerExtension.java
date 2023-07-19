package net.frozenblock.lib.wind.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

@Environment(EnvType.CLIENT)
public interface ClientWindManagerExtension {

	void clientTick();

	void baseTick();

	void receiveSyncPacket(FriendlyByteBuf byteBuf, Minecraft minecraft);
}
