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

package net.frozenblock.lib.item.api.loot;

import java.util.List;
import net.frozenblock.lib.entrypoint.api.CommonEventEntrypoint;
import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.EventRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;

/**
 * A class containing events related to loot tables.
 * <p>
 * On Fabric, {@link #REPLACE}, {@link #MODIFY}, {@link #ALL_LOADED}, and {@link #MODIFY_DROPS} redirect to the
 * matching events in {@code net.fabricmc.fabric.api.loot.v3.LootTableEvents}. On NeoForge, {@link #REPLACE} and
 * {@link #MODIFY} are driven by {@code net.neoforged.neoforge.event.LootTableLoadEvent}, while
 * {@link #ALL_LOADED} and {@link #MODIFY_DROPS} (which have no NeoForge equivalent) are implemented via mixin.
 */
public class FrozenLibLootTableEvents {

	/**
	 * The event that is triggered when an item is generated in a {@link net.minecraft.world.Container}.
	 */
	public static final Event<ItemGeneratedInContainer> ON_ITEM_GENERATED_IN_CONTAINER = EventRegistry.createEnvironmentEvent(
		ItemGeneratedInContainer.class,
		callbacks -> (server, player) -> {
			for (var callback : callbacks) callback.onItemGeneratedInContainer(server, player);
		});

	/**
	 * This event can be used to replace loot tables.
	 * If a loot table is replaced, the iteration will stop for that loot table.
	 */
	public static final Event<Replace> REPLACE = EventRegistry.createEnvironmentEvent(
		Replace.class,
		callbacks -> (key, original, source, registries) -> {
			for (var callback : callbacks) {
				LootTable replaced = callback.replaceLootTable(key, original, source, registries);
				if (replaced != null) return replaced;
			}
			return null;
		});

	/**
	 * This event can be used to modify loot tables.
	 * The main use case is to add items to vanilla or mod loot tables (e.g. modded seeds to grass).
	 */
	public static final Event<Modify> MODIFY = EventRegistry.createEnvironmentEvent(
		Modify.class,
		callbacks -> (key, builder, source, registries) -> {
			for (var callback : callbacks) callback.modifyLootTable(key, builder, source, registries);
		});

	/**
	 * This event can be used for post-processing after all loot tables have been loaded and modified.
	 */
	public static final Event<Loaded> ALL_LOADED = EventRegistry.createEnvironmentEvent(
		Loaded.class,
		callbacks -> (resourceManager, lootRegistry) -> {
			for (var callback : callbacks) callback.onLootTablesLoaded(resourceManager, lootRegistry);
		});

	/**
	 * This event can be used for cases where {@link #MODIFY} and {@link #REPLACE} are inconvenient, such as when
	 * modifying the result of many loot tables that are unknown, and not wishing to add a custom loot function to
	 * every table.
	 */
	public static final Event<ModifyDrops> MODIFY_DROPS = EventRegistry.createEnvironmentEvent(
		ModifyDrops.class,
		callbacks -> (holder, context, drops) -> {
			for (var callback : callbacks) callback.modifyLootTableDrops(holder, context, drops);
		});

	/**
	 * A functional interface representing an item generated in container event.
	 */
	@FunctionalInterface
	public interface ItemGeneratedInContainer extends CommonEventEntrypoint {
		/**
		 * Triggers the event when an item is generated in a {@link Container}.
		 * @param container The {@link Container} the {@link LootTable} is placing an item into.
		 * @param stack The {@link ItemStack} being placed into the {@link Container}.
		 */
		void onItemGeneratedInContainer(Container container, ItemStack stack);
	}

	@FunctionalInterface
	public interface Replace extends CommonEventEntrypoint {
		/**
		 * Replaces loot tables.
		 *
		 * @param key the loot table key
		 * @param original the original loot table
		 * @param source the source of the original loot table
		 * @param registries the holder lookup
		 * @return the new loot table, or null if it wasn't replaced
		 */
		@Nullable
		LootTable replaceLootTable(ResourceKey<LootTable> key, LootTable original, FrozenLibLootTableSource source, HolderLookup.Provider registries);
	}

	@FunctionalInterface
	public interface Modify extends CommonEventEntrypoint {
		/**
		 * Called when a loot table is loading to modify loot tables.
		 *
		 * @param key the loot table key
		 * @param builder a builder of the loot table being loaded
		 * @param source the source of the loot table
		 * @param registries the holder lookup
		 */
		void modifyLootTable(ResourceKey<LootTable> key, LootTable.Builder builder, FrozenLibLootTableSource source, HolderLookup.Provider registries);
	}

	@FunctionalInterface
	public interface Loaded extends CommonEventEntrypoint {
		/**
		 * Called when all loot tables have been loaded and {@link #REPLACE} and {@link #MODIFY} have been invoked.
		 *
		 * @param resourceManager the server resource manager
		 * @param lootRegistry the loot registry
		 */
		void onLootTablesLoaded(ResourceManager resourceManager, Registry<LootTable> lootRegistry);
	}

	@FunctionalInterface
	public interface ModifyDrops extends CommonEventEntrypoint {
		/**
		 * Called after a loot table is finished generating drops to modify drops.
		 * @param holder the loot table's registry holder. This will be a {@link Holder.Reference} if the loot table
		 *                is registered, or a {@link Holder.Direct} if the table is inline
		 * @param context the loot context for the current drops
		 * @param drops the list of drops from the loot table to modify
		 */
		void modifyLootTableDrops(Holder<LootTable> holder, LootContext context, List<ItemStack> drops);
	}
}
