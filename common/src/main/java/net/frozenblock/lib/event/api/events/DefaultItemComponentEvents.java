/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.event.api.events;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.EventRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.Item;

/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
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
 */

@UtilityClass
public final class DefaultItemComponentEvents {
	/**
	 * Event used to add or remove data components to known items.
	 */
	public static final Event<ModifyCallback> MODIFY = EventRegistry.createEnvironmentEvent(ModifyCallback.class, callbacks -> context -> {
		for (ModifyCallback callback : callbacks) callback.modify(context);
	});

	public interface ModifyContext {
		/**
		 * Modify the default data components of the specified item.
		 *
		 * @param itemPredicate A predicate to match items to modify
		 * @param builderConsumer A consumer that provides a {@link DataComponentMap.Builder} to modify the item's components.
		 */
		void modify(Predicate<Item> itemPredicate, ModifyConsumer builderConsumer);

		/**
		 * Modify the default data components of the specified item.
		 *
		 * @param item The item to modify
		 * @param builderConsumer A consumer that provides a {@link DataComponentMap.Builder} to modify the item's components.
		 */
		default void modify(Item item, ModifyConsumer builderConsumer) {
			this.modify(Predicate.isEqual(item), builderConsumer);
		}

		/**
		 * Modify the default data components of the specified items.
		 *
		 * @param items The items to modify
		 * @param builderConsumer A consumer that provides a {@link DataComponentMap.Builder} to modify the item's components.
		 */
		default void modify(Collection<Item> items, ModifyConsumer builderConsumer) {
			this.modify(items::contains, builderConsumer);
		}

		/**
		 * Modify the default data components of the specified item.
		 *
		 * @param itemPredicate A predicate to match items to modify
		 * @param builderConsumer A consumer that provides a {@link DataComponentMap.Builder} to modify the item's components.
		 */
		default void modify(Predicate<Item> itemPredicate, BiConsumer<DataComponentMap.Builder, Item> builderConsumer) {
			this.modify(itemPredicate, ((builder, _lookupProvider, item) -> builderConsumer.accept(builder, item)));
		}

		/**
		 * Modify the default data components of the specified item.
		 *
		 * @param item The item to modify
		 * @param builderConsumer A consumer that provides a {@link DataComponentMap.Builder} to modify the item's components.
		 */
		default void modify(Item item, Consumer<DataComponentMap.Builder> builderConsumer) {
			this.modify(Predicate.isEqual(item), (builder, _item) -> builderConsumer.accept(builder));
		}

		/**
		 * Modify the default data components of the specified items.
		 * @param items The items to modify
		 * @param builderConsumer A consumer that provides a {@link DataComponentMap.Builder} to modify the item's components.
		 */
		default void modify(Collection<Item> items, BiConsumer<DataComponentMap.Builder, Item> builderConsumer) {
			this.modify(items::contains, builderConsumer);
		}
	}

	@FunctionalInterface
	public interface ModifyCallback {
		/**
		 * Modify the default data components of items using the provided {@link ModifyContext} instance.
		 *
		 * @param context The context to modify items
		 */
		void modify(ModifyContext context);
	}

	@FunctionalInterface
	public interface ModifyConsumer {
		/**
		 * A consumer used for modifying the provided {@link DataComponentMap.Builder}.
		 *
		 * @param builder The data component builder for the item.
		 * @param lookupProvider A lookup provider to obtain holders and holder sets from registries.
		 * @param item The item to modify.
		 */
		void modify(DataComponentMap.Builder builder, HolderLookup.Provider lookupProvider, Item item);
	}
}
