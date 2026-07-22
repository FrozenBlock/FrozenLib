package net.frozenblock.lib.entity.impl.variant;

import com.mojang.serialization.MapCodec;
import net.frozenblock.lib.FrozenLibConstants;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.variant.SpawnCondition;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class FrozenLibSpawnConditions {

	public static void init() {
		register("config", ConfigCheck.MAP_CODEC);
	}

	private static void register(String name, MapCodec<? extends SpawnCondition> codec) {
		Registry.register(BuiltInRegistries.SPAWN_CONDITION_TYPE, FrozenLibConstants.id("config"), codec);
	}
}
