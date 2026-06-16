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

package net.frozenblock.lib.loot.impl;

import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootTable;

/**
 * Lets a {@link LootTable} remember its own registry {@link Holder}, populated once the loot table registry
 * has finished loading. Used to resolve the {@code holder} parameter for
 * {@link net.frozenblock.lib.loot.api.FrozenLibLootTableEvents.ModifyDrops}.
 */
public interface FrozenNeoLootTable {
	void frozenLib$setHolder(Holder<LootTable> holder);
}
