package net.frozenblock.lib.advancement.mixin;

import net.frozenblock.lib.advancement.impl.AdvancementInterface;
import net.frozenblock.lib.advancement.impl.AdvancementRequirementsInterface;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(Advancement.class)
public class AdvancementMixin implements AdvancementInterface {
	@Mutable
	@Final
	@Shadow
	private AdvancementRewards rewards;

	@Mutable
	@Final
	@Shadow
	private AdvancementRequirements requirements;

	@Mutable
	@Final
	@Shadow
	private Map<String, Criterion<?>> criteria;

	@Override
	public AdvancementRewards frozenLib$getRewards() {
		return this.rewards;
	}

	@Override
	public void frozenLib$setRewards(AdvancementRewards rewards) {
		this.rewards = rewards;
	}

	@Override
	public AdvancementRequirements frozenLib$getRequirements() {
		return this.requirements;
	}

	@Override
	public AdvancementRequirementsInterface frozenLib$getRequirementsInterface() {
		return AdvancementRequirementsInterface.class.cast(this.requirements);
	}

	@Override
	public void frozenLib$setRequirements(AdvancementRequirements requirements) {
		this.requirements = requirements;
	}

	@Override
	public Map<String, Criterion<?>> frozenLib$getCriteria() {
		return this.criteria;
	}

	@Override
	public void frozenLib$setCriteria(HashMap<String, Criterion<?>> criteria) {
		this.criteria = criteria;
	}
}
