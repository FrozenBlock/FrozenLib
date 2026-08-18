package net.frozenblock.lib.entity.api.platform;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;

public final class FakePlayerApiImpl {
	private FakePlayerApiImpl() {
	}

	public static ServerPlayer create(ServerLevel level, GameProfile profile) {
		return FakePlayerFactory.get(level, profile);
	}

	public static boolean isFakePlayer(Entity entity) {
		return entity instanceof FakePlayer;
	}
}
