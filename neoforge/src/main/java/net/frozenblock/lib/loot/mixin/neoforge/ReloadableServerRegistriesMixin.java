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
import net.frozenblock.lib.item.api.loot.FrozenLibLootTableEvents;
import net.frozenblock.lib.loot.impl.FrozenNeoLootTable;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.Validatable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Drives {@link FrozenLibLootTableEvents#ALL_LOADED}, which has no NeoForge equivalent. NeoForge's own
 * {@code LootTableLoadEvent} already drives {@code REPLACE}/{@code MODIFY} (see {@code NeoLootTableEventBridge}),
 * so this only needs to fire once the loot table registry has finished loading for this reload, and to give each
 * loaded {@link LootTable} its registry holder (for {@code MODIFY_DROPS}).
 */
@Mixin(ReloadableServerRegistries.class)
abstract class ReloadableServerRegistriesMixin {

	@SuppressWarnings("unchecked")
	@Inject(method = "lambda$scheduleRegistryLoad$0", at = @At("RETURN"))
	private static <T extends Validatable> void frozenLib$onLootTablesLoaded(
		LootDataType<T> lootDataType,
		RegistryOps<JsonElement> registryOps,
		ResourceManager resourceManager,
		CallbackInfoReturnable<WritableRegistry<?>> cir
	) {
		if (lootDataType != LootDataType.TABLE) return;

		Registry<LootTable> lootTableRegistry = (Registry<LootTable>) cir.getReturnValue();
		lootTableRegistry.listElements().forEach(reference ->
			((FrozenNeoLootTable) reference.value()).frozenLib$setHolder(reference));

		FrozenLibLootTableEvents.ALL_LOADED.invoker().onLootTablesLoaded(resourceManager, lootTableRegistry);
	}
}
