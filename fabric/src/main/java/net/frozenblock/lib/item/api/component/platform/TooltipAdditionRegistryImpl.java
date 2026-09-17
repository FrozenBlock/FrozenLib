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

package net.frozenblock.lib.item.api.component.platform;

import java.util.function.Supplier;
import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.item.v1.ItemComponentTooltipProviderRegistry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.component.TooltipProvider;

@UtilityClass
public final class TooltipAdditionRegistryImpl {

	public static void addComponentTooltipAtHead(Supplier<DataComponentType<? extends TooltipProvider>> type) {
		ItemComponentTooltipProviderRegistry.addFirst(type.get());
	}

	public static <T> void addComponentTooltipAtHead(Supplier<DataComponentType<T>> type, TooltipProvider.Getter<T> tooltip) {
		ItemComponentTooltipProviderRegistry.addFirst(type.get(), tooltip);
	}

	public static void addComponentTooltipAtTail(Supplier<DataComponentType<? extends TooltipProvider>> type) {
		ItemComponentTooltipProviderRegistry.addLast(type.get());
	}

	public static <T> void addComponentTooltipAtTail(Supplier<DataComponentType<T>> type, TooltipProvider.Getter<T> tooltip) {
		ItemComponentTooltipProviderRegistry.addLast(type.get(), tooltip);
	}

	public static void addComponentTooltipBefore(
		Supplier<DataComponentType<?>> comparedType,
		Supplier<DataComponentType<? extends TooltipProvider>> type
	) {
		ItemComponentTooltipProviderRegistry.addBefore(comparedType.get(), type.get());
	}

	public static <T> void addComponentTooltipBefore(
		Supplier<DataComponentType<T>> comparedType,
		Supplier<DataComponentType<T>> type,
		TooltipProvider.Getter<T> tooltip
	) {
		ItemComponentTooltipProviderRegistry.addBefore(comparedType.get(), type.get(), tooltip);
	}

	public static void addComponentTooltipAfter(
		Supplier<DataComponentType<?>> comparedType,
		Supplier<DataComponentType<? extends TooltipProvider>> type
	) {
		ItemComponentTooltipProviderRegistry.addAfter(comparedType.get(), type.get());
	}

	public static <T> void addComponentTooltipAfter(
		Supplier<DataComponentType<T>> comparedType,
		Supplier<DataComponentType<T>> type,
		TooltipProvider.Getter<T> tooltip
	) {
		ItemComponentTooltipProviderRegistry.addAfter(comparedType.get(), type.get(), tooltip);
	}
}
