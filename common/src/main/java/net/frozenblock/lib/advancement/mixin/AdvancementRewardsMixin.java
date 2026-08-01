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

package net.frozenblock.lib.advancement.mixin;

import java.util.List;
import net.frozenblock.lib.advancement.impl.AdvancementRewardsInterface;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AdvancementRewards.class)
public class AdvancementRewardsMixin implements AdvancementRewardsInterface {
	@Mutable
	@Final
	@Shadow
	private HolderSet<LootTable> loot;

	@Mutable
	@Final
	@Shadow
	private List<ResourceKey<Recipe<?>>> recipes;

	@Mutable
	@Shadow
	@Final
	private int experience;

	@Override
	public HolderSet<LootTable> frozenLib$getLoot() {
		return this.loot;
	}

	@Override
	public void frozenLib$setLoot(HolderSet<LootTable> loot) {
		this.loot = loot;
	}

	@Override
	public List<ResourceKey<Recipe<?>>> frozenLib$getRecipes() {
		return this.recipes;
	}

	@Override
	public void frozenLib$setRecipes(List<ResourceKey<Recipe<?>>> recipes) {
		this.recipes = recipes;
	}

	@Override
	public int frozenLib$getExperience() {
		return this.experience;
	}

	@Override
	public void frozenLib$setExperience(int experience) {
		this.experience = experience;
	}
}
