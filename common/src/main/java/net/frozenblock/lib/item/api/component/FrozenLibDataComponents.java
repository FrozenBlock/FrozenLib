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
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.frozenblock.lib.platform.api.registry.DeferredHolder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;

public final class FrozenLibDataComponents {
	private static final DeferredRegister<DataComponentType<?>> REGISTER = DeferredRegister.create(
		Registries.DATA_COMPONENT_TYPE,
		FrozenLibConstants.MOD_ID
	);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<BundleWeightOverride>> BUNDLE_WEIGHT_OVERRIDE = register(
		"bundle_weight_override",
		builder -> builder.persistent(BundleWeightOverride.CODEC).networkSynchronized(BundleWeightOverride.STREAM_CODEC)
	);

	static {
		REGISTER.register();
	}

	public static void init() {}

	private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String id, UnaryOperator<DataComponentType.Builder<T>> unaryOperator) {
		return REGISTER.register(id, () -> unaryOperator.apply(DataComponentType.builder()).build());
	}
}
