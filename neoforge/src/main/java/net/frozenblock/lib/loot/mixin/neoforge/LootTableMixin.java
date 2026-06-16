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
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.frozenblock.lib.loot.api.FrozenLibLootTableEvents;
import net.frozenblock.lib.loot.impl.FrozenNeoLootTable;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * Drives {@link FrozenLibLootTableEvents#MODIFY_DROPS}, which has no NeoForge equivalent.
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
	private void frozenLib$modifyDrops(LootContext context, Consumer<ItemStack> lootConsumer, Operation<Void> original) {
		if (this.frozenLib$holder == null) {
			this.frozenLib$holder = frozenLib$resolveHolder(context);
		}

		List<ItemStack> drops = new ArrayList<>();
		original.call(context, (Consumer<ItemStack>) drops::add);
		FrozenLibLootTableEvents.MODIFY_DROPS.invoker().modifyLootTableDrops(this.frozenLib$holder, context, drops);
		drops.forEach(lootConsumer);
	}

	@Unique
	private Holder<LootTable> frozenLib$resolveHolder(LootContext context) {
		LootTable self = (LootTable) (Object) this;

		if (context.getLevel() instanceof ServerLevel serverLevel) {
			HolderLookup.Provider provider = serverLevel.getServer().reloadableRegistries().lookup();
			HolderLookup<LootTable> lootTableLookup = provider.lookup(Registries.LOOT_TABLE)
				.orElseThrow(() -> new IllegalStateException("Failed to fetch LootTable provider from HolderLookup.Provider"));

			return lootTableLookup.listElements()
				.filter(it -> it.value().equals(self))
				.<Holder<LootTable>>map(it -> it)
				.findFirst()
				.orElseGet(() -> Holder.direct(self));
		}

		return Holder.direct(self);
	}
}
