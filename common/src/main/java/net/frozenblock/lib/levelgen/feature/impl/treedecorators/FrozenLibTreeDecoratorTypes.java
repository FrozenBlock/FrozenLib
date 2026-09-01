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

package net.frozenblock.lib.levelgen.feature.impl.treedecorators;

import java.util.function.Supplier;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.levelgen.feature.api.treedecorators.ConfigPredicateDecorator;
import net.frozenblock.lib.levelgen.feature.api.treedecorators.ProbabilityDecorator;
import net.frozenblock.lib.platform.api.registry.DeferredHolder;
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class FrozenLibTreeDecoratorTypes {
	private static final DeferredRegister<TreeDecoratorType<?>> REGISTER = DeferredRegister.create(
		Registries.TREE_DECORATOR_TYPE,
		FrozenLibConstants.MOD_ID
	);

	public static final DeferredHolder<TreeDecoratorType<?>, TreeDecoratorType<ConfigPredicateDecorator>> CONFIG_PREDICATE = register(
		"config_predicate",
		() -> new TreeDecoratorType<>(ConfigPredicateDecorator.CODEC)
	);
	public static final DeferredHolder<TreeDecoratorType<?>, TreeDecoratorType<ProbabilityDecorator>> PROBABILITY = register(
		"probability",
		() -> new TreeDecoratorType<>(ProbabilityDecorator.CODEC)
	);

	static {
		REGISTER.register();
	}

	public static void init() {}

	private static <T extends TreeDecorator> DeferredHolder<TreeDecoratorType<?>, TreeDecoratorType<T>> register(String name, Supplier<TreeDecoratorType<T>> supplier) {
		return REGISTER.register(name, supplier);
	}
}
