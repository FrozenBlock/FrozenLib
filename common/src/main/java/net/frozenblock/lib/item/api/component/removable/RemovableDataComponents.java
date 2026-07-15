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

package net.frozenblock.lib.item.api.component.removable;

import java.util.LinkedHashMap;
import java.util.Set;
import net.frozenblock.lib.FrozenLibLogUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;

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

	public static Set<Holder<DataComponentType<?>>> keys() {
		return REMOVABLE_DATA_COMPONENTS.keySet();
	}

	@ApiStatus.Internal
	public static void fixEmptyComponentsAndTags(ItemStack stack) {
		CustomData.update(DataComponents.CUSTOM_DATA, stack, compound -> {
			for (String key : RemovableItemTags.keys()) {
				if (RemovableItemTags.shouldRemoveTagOnStackMerge(key)) compound.remove(key);
			}
		});

		for (Holder<DataComponentType<?>> holder : RemovableDataComponents.keys()) {
			final DataComponentType<?> component = holder.value();
			if (RemovableDataComponents.shouldRemoveComponentOnStackMerge(component)) stack.remove(component);
		}
	}

	@ApiStatus.Internal
	public static void fixEmptyComponentsAndTags(PatchedDataComponentMap components) {
		final CustomData newData = components.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).update(compound -> {
			for (String key : RemovableItemTags.keys()) {
				if (RemovableItemTags.shouldRemoveTagOnStackMerge(key)) compound.remove(key);
			}
		});
		if (newData.isEmpty()) {
			components.remove(DataComponents.CUSTOM_DATA);
		} else {
			components.set(DataComponents.CUSTOM_DATA, newData);
		}

		for (Holder<DataComponentType<?>> holder : RemovableDataComponents.keys()) {
			final DataComponentType<?> component = holder.value();
			if (RemovableDataComponents.shouldRemoveComponentOnStackMerge(component)) components.remove(component);
		}
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
