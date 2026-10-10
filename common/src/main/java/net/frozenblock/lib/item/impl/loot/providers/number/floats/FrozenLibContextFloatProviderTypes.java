package net.frozenblock.lib.item.impl.loot.providers.number.floats;

import com.mojang.serialization.MapCodec;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.config.v2.entry.getter.floats.ConfigValue;
import net.frozenblock.lib.config.v2.entry.getter.floats.ConfigWithFallbackValue;
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProvider;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@UtilityClass
public final class FrozenLibContextFloatProviderTypes {

	public static void init() {
		final DeferredRegister<MapCodec<? extends ContextFloatProvider>> register = DeferredRegister.create(
			Registries.CONTEXT_FLOAT_PROVIDER_TYPE,
			FrozenLibConstants.MOD_ID
		);

		register.register("config_entry", () -> ConfigValue.MAP_CODEC);
		register.register("config_entry_with_fallback", () -> ConfigWithFallbackValue.MAP_CODEC);

		register.register();
	}
}
