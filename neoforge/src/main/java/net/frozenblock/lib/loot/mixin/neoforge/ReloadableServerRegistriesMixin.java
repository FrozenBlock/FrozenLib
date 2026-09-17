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

import com.google.gson.JsonElement;
import net.frozenblock.lib.item.api.loot.LootTableEvents;
import net.frozenblock.lib.loot.impl.FrozenNeoLootTable;
import net.frozenblock.lib.loot.impl.NeoLootUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContextSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Drives {@link LootTableEvents#ALL_LOADED}, which has no NeoForge equivalent.
 * <p>NeoForge's own {@code LootTableLoadEvent} already drives {@code REPLACE}/{@code MODIFY} (see {@code NeoLootTableEventBridge}),
 * so this only needs to fire once the loot table registry has finished loading for this reload, and to give each
 * loaded {@link LootTable} its registry holder (for {@code MODIFY_DROPS}).
 */
@Mixin(ReloadableServerRegistries.class)
abstract class ReloadableServerRegistriesMixin {

	@SuppressWarnings("unchecked")
	@Inject(
		method = "reload",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/resources/RegistryDataLoader;load(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/List;Ljava/util/List;Ljava/util/concurrent/Executor;Ljava/util/List;)Ljava/util/concurrent/CompletableFuture;"
		)
	)
	private static <T extends Validatable> void frozenLib$onLootTablesLoaded(
		ValidationContextSource contextSource, HolderLookup.Provider fullContextWithNewTags, LootDataType<T> lootDataType, CallbackInfo info
	) {
		if (lootDataType != LootDataType.TABLE) return;

		final HolderLookup.RegistryLookup<LootTable> lootTables = fullContextWithNewTags.lookupOrThrow(Registries.LOOT_TABLE);
		lootTables.listElements().forEach(reference ->
			((FrozenNeoLootTable) reference.value()).frozenLib$setHolder(reference));

		LootTableEvents.ALL_LOADED.invoker().onLootTablesLoaded(manager, lootTables);
		NeoLootUtil.SOURCES.remove();
	}
}
