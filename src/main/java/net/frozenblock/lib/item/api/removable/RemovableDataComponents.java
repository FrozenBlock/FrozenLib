/*
 * Copyright 2023-2024 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.item.api.removable;

import java.util.LinkedHashMap;
import java.util.Set;
import net.frozenblock.lib.FrozenLogUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class RemovableDataComponents {

	private static final LinkedHashMap<Holder<DataComponentType<?>>, RemovableDataComponent> REMOVABLE_DATA_COMPONENTS = new LinkedHashMap<>();

	public static void register(DataComponentType<?> component, RemovalPredicate removalPredicate, boolean removeOnStackMerge) {
		ResourceKey<DataComponentType<?>> key = BuiltInRegistries.DATA_COMPONENT_TYPE.getResourceKey(component).orElseThrow();
		Holder<DataComponentType<?>> holder = BuiltInRegistries.DATA_COMPONENT_TYPE.getHolderOrThrow(key);

		REMOVABLE_DATA_COMPONENTS.put(holder, new RemovableDataComponent(holder, removalPredicate, removeOnStackMerge));
	}

	public static boolean canRemoveComponent(DataComponentType<?> component, Level level, Entity entity, int slot, boolean selected) {
		ResourceKey<DataComponentType<?>> key = BuiltInRegistries.DATA_COMPONENT_TYPE.getResourceKey(component).orElseThrow();
		Holder<DataComponentType<?>> holder = BuiltInRegistries.DATA_COMPONENT_TYPE.getHolderOrThrow(key);
		RemovableDataComponent removableDataComponent = REMOVABLE_DATA_COMPONENTS.get(holder);
		if (removableDataComponent != null) {
			return removableDataComponent.shouldRemove(level, entity, slot, selected);
		} else {
			FrozenLogUtils.logError("Unable to find RemovableDataComponent for DataComponent " + key.location() + "!", true, null);
			FrozenLogUtils.logError("Please make sure " + key.location() + " is registered in RemovableDataComponents.class!", true, null);
			return false;
		}
	}

	public static boolean shouldRemoveComponentOnStackMerge(DataComponentType<?> component) {
		ResourceKey<DataComponentType<?>> key = BuiltInRegistries.DATA_COMPONENT_TYPE.getResourceKey(component).orElseThrow();
		Holder<DataComponentType<?>> holder = BuiltInRegistries.DATA_COMPONENT_TYPE.getHolderOrThrow(key);
		RemovableDataComponent removableDataComponent = REMOVABLE_DATA_COMPONENTS.get(holder);
		if (removableDataComponent != null) {
			return removableDataComponent.shouldRemoveOnStackMerge();
		} else {
			FrozenLogUtils.logError("Unable to find RemovableDataComponent data for DataComponent " + key.location() + "!", true, null);
			FrozenLogUtils.logError("Please make sure " + key.location() + " is registered in RemovableDataComponents.class!", true, null);
			return true;
		}
	}

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
		public boolean shouldRemove(Level level, Entity entity, int slot, boolean selected) {
			return this.predicate.shouldRemove(level, entity, slot, selected);
		}

		public boolean shouldRemoveOnStackMerge() {
			return this.removeOnStackMerge;
		}
	}
}
