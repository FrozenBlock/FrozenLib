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

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.frozenblock.lib.item.api.loot.LootTableEvents;
import net.frozenblock.lib.loot.impl.FrozenNeoLootTable;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * Drives {@link LootTableEvents#MODIFY_DROPS}, which has no NeoForge equivalent.
 */
@Mixin(LootTable.class)
abstract class LootTableMixin implements FrozenNeoLootTable {

	@Unique
	@Nullable
	private Holder<LootTable> frozenLib$holder = null;

	@Override
	public void frozenLib$setHolder(Holder<LootTable> holder) {
		this.frozenLib$holder = holder;
	}

	@WrapMethod(method = "getRandomItemsRaw(Lnet/minecraft/world/level/storage/loot/LootContext;Ljava/util/function/Consumer;)V")
	private void frozenLib$modifyDrops(LootContext context, Consumer<ItemStack> output, Operation<Void> original) {
		if (this.frozenLib$holder == null) this.frozenLib$holder = frozenLib$resolveHolder(context);

		final List<ItemStack> drops = new ArrayList<>();
		original.call(context, (Consumer<ItemStack>) drops::add);
		LootTableEvents.MODIFY_DROPS.invoker().modifyLootTableDrops(this.frozenLib$holder, context, drops);
		drops.forEach(output);
	}

	@Unique
	private Holder<LootTable> frozenLib$resolveHolder(LootContext context) {
		final LootTable lootTable = LootTable.class.cast(this);

		if (context.getLevel() instanceof ServerLevel serverLevel) {
			final HolderLookup.Provider registries = serverLevel.getServer().reloadableRegistries().lookup();
			final HolderLookup<LootTable> lootTables = registries.lookup(Registries.LOOT_TABLE)
				.orElseThrow(() -> new IllegalStateException("Failed to fetch LootTable registry from HolderLookup.Provider"));

			return lootTables.listElements()
				.filter(it -> it.value().equals(lootTable))
				.<Holder<LootTable>>map(it -> it)
				.findFirst()
				.orElseGet(() -> Holder.direct(lootTable));
		}

		return Holder.direct(lootTable);
	}
}
