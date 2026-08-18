package net.frozenblock.lib.entity.api.platform;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public final class FakePlayerApiImpl {
	private FakePlayerApiImpl() {
	}

	public static ServerPlayer create(ServerLevel level, GameProfile profile) {
		return FakePlayer.get(level, profile);
	}

	public static boolean isFakePlayer(Entity entity) {
		return entity instanceof FakePlayer;
	}
}
