package net.frozenblock.lib.advancement.impl;

import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import java.util.Map;
import java.util.Optional;

public interface AdvancementInteraction {

	Optional<ResourceLocation> frozenLib$parent();

	Optional<DisplayInfo> frozenLib$display();

	AdvancementRewards frozenLib$rewards();

	Map<String, Criterion<?>> frozenLib$criteria();

	AdvancementRequirements frozenLib$requirements();

	boolean frozenLib$sendsTelemetryEvent();

	Optional<Component> frozenLib$name();

	void frozenLib$setParent(Optional<ResourceLocation> parent);

	void frozenLib$setDisplay(Optional<DisplayInfo> display);

	void frozenLib$setRewards(AdvancementRewards rewards);

	void frozenLib$setCriteria(Map<String, Criterion<?>> criteria);

	void frozenLib$setRequirements(AdvancementRequirements requirements);

	void frozenLib$setSendsTelemetryEvent(boolean sendsTelemetryEvent);

	void frozenLib$setName(Optional<Component> name);
}
