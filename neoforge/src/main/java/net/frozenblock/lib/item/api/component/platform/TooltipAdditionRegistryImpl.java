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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.experimental.UtilityClass;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.component.TooltipProvider;
import net.neoforged.neoforge.common.tooltip.TooltipAppender;
import net.neoforged.neoforge.event.RegisterTooltipAppendersEvent;

@UtilityClass
public final class TooltipAdditionRegistryImpl {
	private static final List<Supplier<Consumer<RegisterTooltipAppendersEvent>>> COMPONENT_TOOLTIP_ADDITIONS = new ArrayList<>();

	public static void addComponentTooltipAtHead(Supplier<DataComponentType<? extends TooltipProvider>> type) {
		COMPONENT_TOOLTIP_ADDITIONS.add(
			() -> event -> {
				final Supplier<Supplier<DataComponentType<? extends TooltipProvider>>> typeSupplier = () -> type;
				event.registerComponentAppenderBeforeAll(
					typeSupplier.get(),
					TooltipAppender.createComponentAppender(typeSupplier.get().get())
				);
			}
		);
	}

	public static <T> void addComponentTooltipAtHead(Supplier<DataComponentType<T>> type, TooltipProvider.Getter<T> tooltip) {
		COMPONENT_TOOLTIP_ADDITIONS.add(
			() -> event -> {
				final Supplier<Supplier<DataComponentType<T>>> typeSupplier = () -> type;
				event.registerComponentAppenderBeforeAll(
					typeSupplier.get(),
					TooltipAppender.createComponentAppender(typeSupplier.get().get(), tooltip)
				);
			}
		);
	}

	public static void addComponentTooltipAtTail(Supplier<DataComponentType<? extends TooltipProvider>> type) {
		COMPONENT_TOOLTIP_ADDITIONS.add(
			() -> event -> {
				final Supplier<Supplier<DataComponentType<? extends TooltipProvider>>> typeSupplier = () -> type;
				event.registerComponentAppenderAfterAll(
					typeSupplier.get(),
					TooltipAppender.createComponentAppender(typeSupplier.get().get())
				);
			}
		);
	}

	public static <T> void addComponentTooltipAtTail(Supplier<DataComponentType<T>> type, TooltipProvider.Getter<T> tooltip) {
		COMPONENT_TOOLTIP_ADDITIONS.add(
			() -> event -> {
				final Supplier<Supplier<DataComponentType<T>>> typeSupplier = () -> type;
				event.registerComponentAppenderAfterAll(
					typeSupplier.get(),
					TooltipAppender.createComponentAppender(typeSupplier.get().get(), tooltip)
				);
			}
		);
	}

	public static void addComponentTooltipBefore(
		Supplier<DataComponentType<?>> comparedType,
		Supplier<DataComponentType<? extends TooltipProvider>> type
	) {
		COMPONENT_TOOLTIP_ADDITIONS.add(
			() -> event -> {
				final Supplier<Supplier<DataComponentType<?>>> comparedTypeSupplier = () -> comparedType;
				final Supplier<Supplier<DataComponentType<? extends TooltipProvider>>> typeSupplier = () -> type;
				event.registerComponentAppenderBefore(
					typeSupplier.get(),
					comparedTypeSupplier.get().get(),
					TooltipAppender.createComponentAppender(typeSupplier.get().get())
				);
			}
		);
	}

	public static <T> void addComponentTooltipBefore(
		Supplier<DataComponentType<T>> comparedType,
		Supplier<DataComponentType<T>> type,
		TooltipProvider.Getter<T> tooltip
	) {
		COMPONENT_TOOLTIP_ADDITIONS.add(
			() -> event -> {
				final Supplier<Supplier<DataComponentType<T>>> comparedTypeSupplier = () -> comparedType;
				final Supplier<Supplier<DataComponentType<T>>> typeSupplier = () -> type;
				event.registerComponentAppenderBefore(
					typeSupplier.get(),
					comparedTypeSupplier.get().get(),
					TooltipAppender.createComponentAppender(typeSupplier.get().get(), tooltip)
				);
			}
		);
	}

	public static void addComponentTooltipAfter(
		Supplier<DataComponentType<?>> comparedType,
		Supplier<DataComponentType<? extends TooltipProvider>> type
	) {
		COMPONENT_TOOLTIP_ADDITIONS.add(
			() -> event -> {
				final Supplier<Supplier<DataComponentType<?>>> comparedTypeSupplier = () -> comparedType;
				final Supplier<Supplier<DataComponentType<? extends TooltipProvider>>> typeSupplier = () -> type;
				event.registerComponentAppenderAfter(
					typeSupplier.get(),
					comparedTypeSupplier.get().get(),
					TooltipAppender.createComponentAppender(typeSupplier.get().get())
				);
			}
		);
	}

	public static <T> void addComponentTooltipAfter(
		Supplier<DataComponentType<T>> comparedType,
		Supplier<DataComponentType<T>> type,
		TooltipProvider.Getter<T> tooltip
	) {
		COMPONENT_TOOLTIP_ADDITIONS.add(
			() -> event -> {
				final Supplier<Supplier<DataComponentType<T>>> comparedTypeSupplier = () -> comparedType;
				final Supplier<Supplier<DataComponentType<T>>> typeSupplier = () -> type;
				event.registerComponentAppenderAfter(
					typeSupplier.get(),
					comparedTypeSupplier.get().get(),
					TooltipAppender.createComponentAppender(typeSupplier.get().get(), tooltip)
				);
			}
		);
	}

	public static void flushComponentTooltipAdditions(RegisterTooltipAppendersEvent event) {
		for (Supplier<Consumer<RegisterTooltipAppendersEvent>> consumer : COMPONENT_TOOLTIP_ADDITIONS) {
			consumer.get().accept(event);
		}
		COMPONENT_TOOLTIP_ADDITIONS.clear();
	}
}
