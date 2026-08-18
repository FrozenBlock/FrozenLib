package net.frozenblock.lib.entity.api;

import com.mojang.authlib.GameProfile;
import lombok.experimental.UtilityClass;
import net.mehvahdjukaar.candlelight.api.PlatformImpl;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

@UtilityClass
public final class FakePlayerApi {

	@PlatformImpl
	public static ServerPlayer create(ServerLevel level, GameProfile profile) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static boolean isFakePlayer(Entity entity) {
		throw new AssertionError();
	}
}
