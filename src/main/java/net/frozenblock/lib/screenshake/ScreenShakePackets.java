package net.frozenblock.lib.screenshake;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.lib.FrozenMain;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public final class ScreenShakePackets {

	private ScreenShakePackets() {
		throw new UnsupportedOperationException("ScreenShakePackets contains only static declarations.");
	}

	public static void createScreenShakePacket(Level level, float intensity, double x, double y, double z, float maxDistance) {
		createScreenShakePacket(level, intensity, 5, 1, x, y, z, maxDistance);
	}

	public static void createScreenShakePacket(Level level, float intensity, int duration, double x, double y, double z, float maxDistance) {
		createScreenShakePacket(level, intensity, duration, 1, x, y, z, maxDistance);
	}

	public static void createScreenShakePacket(Level level, float intensity, int duration, int falloffStart, double x, double y, double z, float maxDistance) {
		if (!level.isClientSide) {
			FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
			byteBuf.writeFloat(intensity);
			byteBuf.writeInt(duration);
			byteBuf.writeInt(falloffStart);
			byteBuf.writeDouble(x);
			byteBuf.writeDouble(y);
			byteBuf.writeDouble(z);
			byteBuf.writeFloat(maxDistance);
			for (ServerPlayer player : PlayerLookup.world((ServerLevel) level)) {
				ServerPlayNetworking.send(player, FrozenMain.SCREEN_SHAKE_PACKET, byteBuf);
			}
		}
	}
}
