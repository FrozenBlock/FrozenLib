/*
 * Copyright (C) 2024-2025 FrozenBlock
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

package net.frozenblock.lib.item.api;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class ItemTooltipAdditionAPI {
	private static final Map<TooltipCondition, List<Component>> ADDITIONAL_TOOLTIPS = new Object2ObjectLinkedOpenHashMap<>();

	/**
	 * Registers text to render as a tooltip for an item when certain conditions are met.
	 * @param tooltip The text, in {@link Component} form, to render.
	 * @param condition The conditions under which the tooltip will render.
	 */
	public static void addTooltip(Component tooltip, TooltipCondition condition) {
		List<Component> tooltips = List.of(tooltip);
		addTooltips(tooltips, condition);
	}

	/**
	 * Registers a list of individual texts to render as tooltips for an item when certain conditions are met.
	 * @param tooltips A list of text, in {@link Component} form, to render.
	 * @param condition The conditions under which the tooltips will render.
	 */
	public static void addTooltips(List<Component> tooltips, TooltipCondition condition) {
		List<Component> tooltipList = ADDITIONAL_TOOLTIPS.getOrDefault(condition, new ArrayList<>());
		tooltipList.addAll(tooltips);
		ADDITIONAL_TOOLTIPS.put(condition, tooltipList);
	}

	public static @NotNull Optional<List<Component>> getTooltipsForItemStack(ItemStack stack) {
		List<Component> tooltips = new ArrayList<>();
		ADDITIONAL_TOOLTIPS.forEach((condition, tooltipList) -> {
			if (condition.test(stack)) tooltips.addAll(tooltipList);
		});
		if (tooltips.isEmpty()) return Optional.empty();
		return Optional.of(tooltips);
	}

	@FunctionalInterface
	public interface TooltipCondition {
		boolean test(ItemStack stack);
	}
}
