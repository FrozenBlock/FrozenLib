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

package net.frozenblock.lib.item.mixin.registry;

import java.util.List;
import net.frozenblock.lib.item.impl.registry.FrozenLibPotionBrewingBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PotionBrewing.Builder.class)
public abstract class PotionBrewingBuilderMixin implements FrozenLibPotionBrewingBuilder {

	@Shadow
	@Final
	public FeatureFlagSet enabledFeatures;

	@Shadow
	@Final
	public List<PotionBrewing.Mix<Item>> containerMixes;

	@Shadow
	@Final
	public List<PotionBrewing.Mix<Potion>> potionMixes;

	@Shadow
	public static void expectPotion(Item from) {
	}

	@Override
	public void frozenLib$registerItemRecipe(Item input, Ingredient ingredient, Item output) {
		if (input.isEnabled(this.enabledFeatures) && output.isEnabled(this.enabledFeatures)) {
			expectPotion(input);
			expectPotion(output);
			this.containerMixes.add(new PotionBrewing.Mix<>(input.builtInRegistryHolder(), ingredient, output.builtInRegistryHolder()));
		}
	}

	@Override
	public void frozenLib$registerPotionRecipe(Holder<Potion> input, Ingredient ingredient, Holder<Potion> output) {
		if (input.value().isEnabled(this.enabledFeatures) && output.value().isEnabled(this.enabledFeatures)) {
			this.potionMixes.add(new PotionBrewing.Mix<>(input, ingredient, output));
		}
	}

	@Override
	public void frozenLib$registerRecipes(Ingredient ingredient, Holder<Potion> potion) {
		if (potion.value().isEnabled(this.enabledFeatures)) {
			this.frozenLib$registerPotionRecipe(Potions.WATER, ingredient, Potions.MUNDANE);
			this.frozenLib$registerPotionRecipe(Potions.AWKWARD, ingredient, potion);
		}
	}

	@Override
	public FeatureFlagSet frozenLib$getEnabledFeatures() {
		return this.enabledFeatures;
	}
}
