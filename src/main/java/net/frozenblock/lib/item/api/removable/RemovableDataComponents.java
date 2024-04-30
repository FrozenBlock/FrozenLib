/*
 * Copyright 2023 FrozenBlock
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
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
