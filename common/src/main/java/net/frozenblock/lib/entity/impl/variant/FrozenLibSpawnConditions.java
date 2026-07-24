package net.frozenblock.lib.entity.impl.variant;

import com.mojang.serialization.MapCodec;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.entity.api.variant.CompoundCheck;
import net.frozenblock.lib.entity.api.variant.ConfigCheck;
import net.frozenblock.lib.platform.api.registry.FrozenDeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.variant.SpawnCondition;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class FrozenLibSpawnConditions {
	private static final FrozenDeferredRegister<MapCodec<? extends SpawnCondition>> REGISTER = FrozenDeferredRegister.create(
		Registries.SPAWN_CONDITION_TYPE,
		FrozenLibConstants.MOD_ID
	);

	static {
		REGISTER.register("compound", () -> CompoundCheck.MAP_CODEC);
		REGISTER.register("config", () -> ConfigCheck.MAP_CODEC);
		REGISTER.register();
	}

	public static void init() {}
}
