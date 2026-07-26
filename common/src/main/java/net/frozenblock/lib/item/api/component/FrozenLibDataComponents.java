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

package net.frozenblock.lib.item.api.component;

import java.util.function.UnaryOperator;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.platform.api.registry.FrozenDeferredRegister;
import net.frozenblock.lib.platform.api.registry.FrozenHolder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;

public final class FrozenLibDataComponents {
	private static final FrozenDeferredRegister<DataComponentType<?>> REGISTER = FrozenDeferredRegister.create(
		Registries.DATA_COMPONENT_TYPE,
		FrozenLibConstants.MOD_ID
	);

	public static final FrozenHolder<DataComponentType<?>, DataComponentType<BundleWeightOverride>> BUNDLE_WEIGHT_OVERRIDE = register(
		"bundle_weight_override",
		builder -> builder.persistent(BundleWeightOverride.CODEC).networkSynchronized(BundleWeightOverride.STREAM_CODEC)
	);

	static {
		REGISTER.register();
	}

	public static void init() {}

	private static <T> FrozenHolder<DataComponentType<?>, DataComponentType<T>> register(String id, UnaryOperator<DataComponentType.Builder<T>> unaryOperator) {
		return REGISTER.register(id, () -> unaryOperator.apply(DataComponentType.builder()).build());
	}
}
