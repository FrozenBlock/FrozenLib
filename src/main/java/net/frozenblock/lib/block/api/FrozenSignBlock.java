/*
 * Copyright 2023-2024 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.block.api;

import java.util.Objects;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.storage.loot.LootTable;

public class FrozenSignBlock extends StandingSignBlock {
    public final ResourceKey<LootTable> lootTable;

    public FrozenSignBlock(Properties settings, WoodType signType, ResourceKey<LootTable> lootTable) {
        super(signType, settings);
        this.lootTable = lootTable;
    }

    @Override
	public ResourceKey<LootTable> getLootTable() {
        if (!Objects.equals(this.drops, this.lootTable)) {
            this.drops = this.lootTable;
        }

		assert this.drops != null;
		return this.drops;
    }
}
