package net.frozenblock.lib.item.impl.loot.providers.number.ints;

import com.mojang.serialization.MapCodec;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.config.v2.entry.getter.ints.ConfigValue;
import net.frozenblock.lib.config.v2.entry.getter.ints.ConfigWithFallbackValue;
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@UtilityClass
public final class FrozenLibContextIntProviderTypes {

	public static void init() {
		final DeferredRegister<MapCodec<? extends ContextIntProvider>> register = DeferredRegister.create(
			Registries.CONTEXT_INT_PROVIDER_TYPE,
			FrozenLibConstants.MOD_ID
		);

		register.register("config_entry", () -> ConfigValue.MAP_CODEC);
		register.register("config_entry_with_fallback", () -> ConfigWithFallbackValue.MAP_CODEC);

		register.register();
	}
}
