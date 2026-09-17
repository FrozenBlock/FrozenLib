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

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.experimental.UtilityClass;
import net.mehvahdjukaar.candlelight.api.PlatformImpl;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TooltipProvider;

@UtilityClass
public final class TooltipAdditionRegistry {
	private static final Map<Predicate<ItemStack>, List<Component>> ADDITIONAL_TOOLTIPS = new Object2ObjectLinkedOpenHashMap<>();

	/**
	 * Registers text to render as a tooltip for an item when certain conditions are met.
	 * @param tooltip The text, in {@link Component} form, to render.
	 * @param condition The conditions under which the tooltip will render.
	 */
	public static void addTooltip(Component tooltip, Predicate<ItemStack> condition) {
		final List<Component> tooltips = List.of(tooltip);
		addTooltips(tooltips, condition);
	}

	/**
	 * Registers a list of individual texts to render as tooltips for an item when certain conditions are met.
	 * @param tooltips A list of text, in {@link Component} form, to render.
	 * @param condition The conditions under which the tooltips will render.
	 */
	public static void addTooltips(List<Component> tooltips, Predicate<ItemStack> condition) {
		final List<Component> tooltipList = ADDITIONAL_TOOLTIPS.getOrDefault(condition, new ArrayList<>());
		tooltipList.addAll(tooltips);
		ADDITIONAL_TOOLTIPS.put(condition, tooltipList);
	}

	@PlatformImpl
	public static void addComponentTooltipAtHead(Supplier<DataComponentType<? extends TooltipProvider>> type) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static <T> void addComponentTooltipAtHead(Supplier<DataComponentType<T>> type, TooltipProvider.Getter<T> tooltip) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static void addComponentTooltipAtTail(Supplier<DataComponentType<? extends TooltipProvider>> type) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static <T> void addComponentTooltipAtTail(Supplier<DataComponentType<T>> type, TooltipProvider.Getter<T> tooltip) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static void addComponentTooltipBefore(
		Supplier<DataComponentType<?>> comparedType,
		Supplier<DataComponentType<? extends TooltipProvider>> type
	) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static <T> void addComponentTooltipBefore(
		Supplier<DataComponentType<T>> comparedType,
		Supplier<DataComponentType<T>> type,
		TooltipProvider.Getter<T> tooltip
	) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static void addComponentTooltipAfter(
		Supplier<DataComponentType<?>> comparedType,
		Supplier<DataComponentType<? extends TooltipProvider>> type
	) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static <T> void addComponentTooltipAfter(
		Supplier<DataComponentType<T>> comparedType,
		Supplier<DataComponentType<T>> type,
		TooltipProvider.Getter<T> tooltip
	) {
		throw new AssertionError();
	}

	public static Optional<List<Component>> getTooltipsForItemStack(ItemStack stack) {
		final List<Component> tooltips = new ArrayList<>();
		ADDITIONAL_TOOLTIPS.forEach((condition, tooltipList) -> {
			if (condition.test(stack)) tooltips.addAll(tooltipList);
		});
		if (tooltips.isEmpty()) return Optional.empty();
		return Optional.of(tooltips);
	}
}
