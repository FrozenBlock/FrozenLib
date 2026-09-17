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

package net.frozenblock.lib.loot.mixin.neoforge;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.frozenblock.lib.item.api.loot.LootTableEvents;
import net.frozenblock.lib.loot.impl.FrozenNeoLootTable;
import net.frozenblock.lib.loot.impl.NeoLootUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Drives {@link LootTableEvents#ALL_LOADED}, which has no NeoForge equivalent.
 * <p>NeoForge's own {@code LootTableLoadEvent} already drives {@code REPLACE}/{@code MODIFY} (see {@code NeoLootTableEventBridge}),
 * so this only needs to fire once the loot table registry has finished loading for this reload, and to give each
 * loaded {@link LootTable} its registry holder (for {@code MODIFY_DROPS}).
 */
@Mixin(ReloadableServerRegistries.class)
abstract class ReloadableServerRegistriesMixin {

	@SuppressWarnings("unchecked")
	@WrapOperation(
		method = "reload",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/resources/RegistryDataLoader;load(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/List;Ljava/util/List;Ljava/util/concurrent/Executor;Ljava/util/List;)Ljava/util/concurrent/CompletableFuture;"
		)
	)
	private static CompletableFuture<RegistryAccess.Frozen> frozenLib$onLootTablesLoaded(
		ResourceManager manager,
		List<HolderLookup.RegistryLookup<?>> contextRegistries,
		List<RegistryDataLoader.RegistryData<?>> registriesToLoad,
		Executor executor,
		List<Registry.PendingTags<?>> updatedContextTags,
		Operation<CompletableFuture<RegistryAccess.Frozen>> original
	) {
		return original.call(manager, contextRegistries, registriesToLoad, executor, updatedContextTags)
			.thenApply(registries -> {
				Registry<LootTable> lootTableRegistry = registries.lookupOrThrow(Registries.LOOT_TABLE);
				LootTableEvents.ALL_LOADED.invoker().onLootTablesLoaded(manager, lootTableRegistry);
				lootTableRegistry.listElements().forEach(reference -> ((FrozenNeoLootTable) reference.value()).frozenLib$setHolder(reference));
				return registries;
			}).whenComplete((registries, throwable) -> NeoLootUtil.SOURCES.remove());
	}
}
