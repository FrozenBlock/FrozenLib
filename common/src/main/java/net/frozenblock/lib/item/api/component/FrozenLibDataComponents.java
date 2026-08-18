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
import net.frozenblock.lib.platform.api.registry.DeferredDataComponentType;
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.minecraft.core.component.DataComponentType;

public final class FrozenLibDataComponents {
	private static final DeferredRegister.DataComponents REGISTER = DeferredRegister.createDataComponents(FrozenLibConstants.MOD_ID);

	public static final DeferredDataComponentType<DamageOnConsume> DAMAGE_ON_CONSUME = register(
		"damage_on_consume",
		builder -> builder.persistent(DamageOnConsume.CODEC).networkSynchronized(DamageOnConsume.STREAM_CODEC).cacheEncoding()
	);
	public static final DeferredDataComponentType<BundleWeightOverride> BUNDLE_WEIGHT_OVERRIDE = register(
		"bundle_weight_override",
		builder -> builder.persistent(BundleWeightOverride.CODEC).networkSynchronized(BundleWeightOverride.STREAM_CODEC)
	);

	static {
		REGISTER.register();
	}

	public static void init() {}

	private static <T> DeferredDataComponentType<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
		return REGISTER.register(name, builder);
	}
}
