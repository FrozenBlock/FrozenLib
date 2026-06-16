package net.frozenblock.lib.advancement.mixin;

import net.frozenblock.lib.advancement.impl.AdvancementRewardsInterface;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import java.util.List;

@Mixin(AdvancementRewards.class)
public class AdvancementRewardsMixin implements AdvancementRewardsInterface {
	@Mutable
	@Final
	@Shadow
	private List<ResourceKey<LootTable>> loot;

	@Mutable
	@Final
	@Shadow
	private List<ResourceKey<Recipe<?>>> recipes;

	@Override
	public List<ResourceKey<LootTable>> frozenLib$getLoot() {
		return this.loot;
	}

	@Override
	public void frozenLib$setLoot(List<ResourceKey<LootTable>> loot) {
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
}
