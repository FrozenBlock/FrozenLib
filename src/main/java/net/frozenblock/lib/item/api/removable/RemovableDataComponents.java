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

package net.frozenblock.lib.item.api.removable;

import java.util.LinkedHashMap;
import java.util.Set;
import net.frozenblock.lib.FrozenLibLogUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Contract;

public class RemovableDataComponents {
	private static final LinkedHashMap<Holder<DataComponentType<?>>, RemovableDataComponent> REMOVABLE_DATA_COMPONENTS = new LinkedHashMap<>();

	public static void register(DataComponentType<?> component, RemovalPredicate removalPredicate, boolean removeOnStackMerge) {
		final ResourceKey<DataComponentType<?>> key = BuiltInRegistries.DATA_COMPONENT_TYPE.getResourceKey(component).orElseThrow();
		final Holder<DataComponentType<?>> holder = BuiltInRegistries.DATA_COMPONENT_TYPE.getOrThrow(key);

		REMOVABLE_DATA_COMPONENTS.put(holder, new RemovableDataComponent(holder, removalPredicate, removeOnStackMerge));
	}

	public static boolean canRemoveComponent(DataComponentType<?> component, Level level, Entity entity, EquipmentSlot slot) {
		final ResourceKey<DataComponentType<?>> key = BuiltInRegistries.DATA_COMPONENT_TYPE.getResourceKey(component).orElseThrow();
		final Holder<DataComponentType<?>> holder = BuiltInRegistries.DATA_COMPONENT_TYPE.getOrThrow(key);
		final RemovableDataComponent removableDataComponent = REMOVABLE_DATA_COMPONENTS.get(holder);
		if (removableDataComponent != null) return removableDataComponent.shouldRemove(level, entity, slot);

		FrozenLibLogUtils.logError("Unable to find RemovableDataComponent for DataComponent " + key.identifier() + "!", true, null);
		FrozenLibLogUtils.logError("Please make sure " + key.identifier() + " is registered in RemovableDataComponents.class!", true, null);
		return false;
	}

	public static boolean shouldRemoveComponentOnStackMerge(DataComponentType<?> component) {
		final ResourceKey<DataComponentType<?>> key = BuiltInRegistries.DATA_COMPONENT_TYPE.getResourceKey(component).orElseThrow();
		final Holder<DataComponentType<?>> holder = BuiltInRegistries.DATA_COMPONENT_TYPE.getOrThrow(key);
		final RemovableDataComponent removableDataComponent = REMOVABLE_DATA_COMPONENTS.get(holder);
		if (removableDataComponent != null) return removableDataComponent.shouldRemoveOnStackMerge();

		FrozenLibLogUtils.logError("Unable to find RemovableDataComponent data for DataComponent " + key.identifier() + "!", true, null);
		FrozenLibLogUtils.logError("Please make sure " + key.identifier() + " is registered in RemovableDataComponents.class!", true, null);
		return true;
	}

	@Contract(pure = true)
	public static Set<Holder<DataComponentType<?>>> keys() {
		return REMOVABLE_DATA_COMPONENTS.keySet();
	}

	public static class RemovableDataComponent implements RemovalPredicate {
		private final Holder<DataComponentType<?>> component;
		private final RemovalPredicate predicate;
		private final boolean removeOnStackMerge;

		public RemovableDataComponent(Holder<DataComponentType<?>> component, RemovalPredicate predicate, boolean removeOnStackMerge) {
			this.component = component;
			this.predicate = predicate;
			this.removeOnStackMerge = removeOnStackMerge;
		}

		public Holder<DataComponentType<?>> getComponent() {
			return this.component;
		}

		@Override
		public boolean shouldRemove(Level level, Entity entity, EquipmentSlot slot) {
			return this.predicate.shouldRemove(level, entity, slot);
		}

		public boolean shouldRemoveOnStackMerge() {
			return this.removeOnStackMerge;
		}
	}
}
