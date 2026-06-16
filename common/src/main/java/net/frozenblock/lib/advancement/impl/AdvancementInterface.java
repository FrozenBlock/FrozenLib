package net.frozenblock.lib.advancement.impl;

import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.storage.loot.LootTable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface AdvancementInterface {
	AdvancementRewards frozenLib$getRewards();
	void frozenLib$setRewards(AdvancementRewards rewards);

	AdvancementRequirements frozenLib$getRequirements();
	AdvancementRequirementsInterface frozenLib$getRequirementsInterface();
	void frozenLib$setRequirements(AdvancementRequirements requirements);

	Map<String, Criterion<?>> frozenLib$getCriteria();
	void frozenLib$setCriteria(HashMap<String, Criterion<?>> criteria);
}
