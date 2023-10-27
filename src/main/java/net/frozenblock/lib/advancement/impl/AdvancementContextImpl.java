package net.frozenblock.lib.advancement.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.frozenblock.lib.advancement.api.AdvancementContext;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public record AdvancementContextImpl(AdvancementHolder holder) implements AdvancementContext {

	private static AdvancementInteraction interaction(Advancement advancement) {
		return (AdvancementInteraction) (Object) advancement;
	}

	private AdvancementInteraction getInteraction() {
		return interaction(this.holder().value());
	}

	@Override
	public ResourceLocation key() {
		return holder.id();
	}

	@Override
	public Optional<ResourceLocation> parent() {
		return holder().value().parent();
	}

	@Override
	public Optional<DisplayInfo> display() {
		return holder().value().display();
	}

	@Override
	public AdvancementRewards rewards() {
		return interaction(holder().value()).frozenLib$rewards();
	}

	@Override
	public Map<String, Criterion<?>> criteria() {
		return holder().value().criteria();
	}

	@Override
	public AdvancementRequirements requirements() {
		return holder().value().requirements();
	}

	@Override
	public boolean sendsTelemetryEvent() {
		return holder().value().sendsTelemetryEvent();
	}

	@Override
	public Optional<Component> name() {
		return holder().value().name();
	}

	@Override
	public void addCriteria(String key, Criterion<?> criteria) {
		if (!(holder().value().criteria() instanceof HashMap<String, Criterion<?>>)) {
			getInteraction().frozenLib$setCriteria(new HashMap<>(holder().value().criteria()));
		}
		holder().value().criteria().putIfAbsent(key, criteria);
	}

	@Override
	public void addRequirements(AdvancementRequirements requirements) {
		List<String[]> list = new ArrayList<>();
		list.addAll(Arrays.stream(getInteraction().frozenLib$requirements().requirements).toList());
		list.addAll(Arrays.stream(requirements.requirements).toList());
		getInteraction().frozenLib$requirements().requirements = list.toArray(new String[][]{});
	}

	@Override
	public void addLootTables(List<ResourceLocation> lootTables) {
		var rewards = this.rewards();
		List<ResourceLocation> newLoot = new ArrayList<>(Arrays.stream(rewards.loot).toList());
		newLoot.addAll(lootTables);
		rewards.loot = newLoot.toArray(new ResourceLocation[]{});
	}

	@Override
	public void addRecipes(Collection<ResourceLocation> recipes) {
		var rewards = this.rewards();
		List<ResourceLocation> newLoot = new ArrayList<>(Arrays.stream(rewards.recipes).toList());
		newLoot.addAll(recipes);
		rewards.recipes = newLoot.toArray(new ResourceLocation[]{});
	}

	@Override
	public void setExperience(int experience) {
		var rewards = this.rewards();
		rewards.experience = experience;
	}

	@Override
	public void setTelemetry(boolean telemetry) {
		getInteraction().frozenLib$setSendsTelemetryEvent(telemetry);
	}
}
