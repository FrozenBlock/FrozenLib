/*
 * Copyright (C) 2024-2026 FrozenBlock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.registry;

import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.config.v2.entry.predicates.ConfigPredicate;
import net.frozenblock.lib.integration.api.ModIntegration;
import net.frozenblock.lib.integration.api.ModIntegrationSupplier;
import net.frozenblock.lib.levelgen.music.pitch.provider.PitchProvider;
import net.frozenblock.lib.platform.RegistryHelper;
import net.frozenblock.lib.sound.api.predicate.SoundPredicate;
import net.frozenblock.lib.sound.api.type.MovingSoundType;
import net.frozenblock.lib.wind.disturbance.WindDisturbanceType;
import net.frozenblock.lib.wind.extension.WindManagerExtensionType;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.Entity;

@UtilityClass
public final class FrozenLibBuiltInRegistries {
	public static final MappedRegistry<ModIntegrationSupplier<?>> MOD_INTEGRATION = RegistryHelper.createSimple(
		FrozenLibRegistries.MOD_INTEGRATION,
		Lifecycle.stable(),
		false,
		registry -> Registry.register(
			registry,
			FrozenLibConstants.id("dummy"),
			new ModIntegrationSupplier<>(
				() -> new ModIntegration("frozenlib_dummy") {
					@Override
					public void init() {}
				},
				"frozenlib_dummy"
			)
		)
	);
	public static final MappedRegistry<Supplier<SoundPredicate<?>>> SOUND_PREDICATE_TYPE = RegistryHelper.createSimple(
		FrozenLibRegistries.SOUND_PREDICATE_TYPE,
		Lifecycle.stable(),
		true,
		registry -> Registry.register(registry, FrozenLibConstants.id("always_true"), () -> new SoundPredicate<>() {
			@Override
			public boolean test(Entity entity) {
				return true;
			}
		})
	);
	public static final MappedRegistry<WindManagerExtensionType<?>> WIND_MANAGER_EXTENSION_TYPE = RegistryHelper.createSimple(
		FrozenLibRegistries.WIND_MANAGER_EXTENSION_TYPE,
		Lifecycle.stable(),
		true
	);
	public static final MappedRegistry<WindDisturbanceType<?>> WIND_DISTURBANCE_TYPE = RegistryHelper.createSimple(
		FrozenLibRegistries.WIND_DISTURBANCE_TYPE,
		Lifecycle.stable(),
		true
	);
	public static final MappedRegistry<MapCodec<? extends ConfigPredicate>> CONFIG_PREDICATE_TYPE = RegistryHelper.createSimple(
		FrozenLibRegistries.CONFIG_PREDICATE_TYPE,
		Lifecycle.stable()
	);
	public static final MappedRegistry<MapCodec<? extends PitchProvider>> PITCH_PROVIDER_TYPE = RegistryHelper.createSimple(
		FrozenLibRegistries.PITCH_PROVIDER_TYPE,
		Lifecycle.stable()
	);
	public static final MappedRegistry<MovingSoundType<?>> MOVING_SOUND_TYPE = RegistryHelper.createSimple(
		FrozenLibRegistries.MOVING_SOUND_TYPE,
		Lifecycle.stable()
	);

	public static void init() {}
}
