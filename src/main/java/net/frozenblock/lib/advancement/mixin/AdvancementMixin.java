package net.frozenblock.lib.advancement.mixin;

import net.frozenblock.lib.advancement.impl.AdvancementInteraction;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import java.util.Map;
import java.util.Optional;

@Mixin(Advancement.class)
public class AdvancementMixin implements AdvancementInteraction {
	@Mutable
	@Final
	@Shadow
	private Optional<ResourceLocation> parent;

	@Mutable
	@Shadow
	@Final
	private Optional<DisplayInfo> display;

	@Mutable
	@Shadow
	@Final
	private AdvancementRewards rewards;

	@Mutable
	@Shadow
	@Final
	private Map<String, Criterion<?>> criteria;

	@Mutable
	@Shadow
	@Final
	private AdvancementRequirements requirements;

	@Mutable
	@Shadow
	@Final
	private boolean sendsTelemetryEvent;

	@Mutable
	@Shadow
	@Final
	private Optional<Component> name;

	@Override
	public Optional<ResourceLocation> frozenLib$parent() {
		return this.parent;
	}

	@Override
	public Optional<DisplayInfo> frozenLib$display() {
		return this.display;
	}

	@Override
	public AdvancementRewards frozenLib$rewards() {
		return this.rewards;
	}

	@Override
	public Map<String, Criterion<?>> frozenLib$criteria() {
		return this.criteria;
	}

	@Override
	public AdvancementRequirements frozenLib$requirements() {
		return this.requirements;
	}

	@Override
	public boolean frozenLib$sendsTelemetryEvent() {
		return this.sendsTelemetryEvent;
	}

	@Override
	public Optional<Component> frozenLib$name() {
		return this.name;
	}

	@Override
	public void frozenLib$setParent(Optional<ResourceLocation> parent) {
		this.parent = parent;
	}

	@Override
	public void frozenLib$setDisplay(Optional<DisplayInfo> display) {
		this.display = display;
	}

	@Override
	public void frozenLib$setRewards(AdvancementRewards rewards) {
		this.rewards = rewards;
	}

	@Override
	public void frozenLib$setCriteria(Map<String, Criterion<?>> criteria) {
		this.criteria = criteria;
	}

	@Override
	public void frozenLib$setRequirements(AdvancementRequirements requirements) {
		this.requirements = requirements;
	}

	@Override
	public void frozenLib$setSendsTelemetryEvent(boolean sendsTelemetryEvent) {
		this.sendsTelemetryEvent = sendsTelemetryEvent;
	}

	@Override
	public void frozenLib$setName(Optional<Component> name) {
		this.name = name;
	}
}
