/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.levelgen.feature.impl.foliageplacers;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.levelgen.feature.api.foliageplacers.ConditionalFoliagePlacer;
import net.frozenblock.lib.platform.api.registry.DeferredHolder;
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@UtilityClass
public final class FrozenLibFoliagePlacerTypes {
	private static final DeferredRegister<FoliagePlacerType<?>> REGISTER = DeferredRegister.create(
		Registries.FOLIAGE_PLACER_TYPE,
		FrozenLibConstants.MOD_ID
	);

	public static final DeferredHolder<FoliagePlacerType<?>, FoliagePlacerType<ConditionalFoliagePlacer>> CONDITIONAL_FOLIAGE_PLACER = REGISTER.register(
		"conditional_foliage_placer",
		() -> new FoliagePlacerType<>(ConditionalFoliagePlacer.CODEC)
	);

	static {
		REGISTER.register();
	}

	public static void init() {}
}
