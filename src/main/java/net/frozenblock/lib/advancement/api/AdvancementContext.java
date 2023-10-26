package net.frozenblock.lib.advancement.api;

import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AdvancementContext {

	ResourceLocation key();

	Optional<ResourceLocation> parent();

	Optional<DisplayInfo> display();

	AdvancementRewards rewards();

	Map<String, Criterion<?>> criteria();

	AdvancementRequirements requirements();

	boolean sendsTelemetryEvent();

	Optional<Component> name();

	void addCriteria(String key, Criterion<?> criteria);

	void addRequirements(AdvancementRequirements requirements);

	void addLoot(List<Item> loot);

	void addRecipes(Collection<ResourceLocation> recipes);

	void setExperience(int experience);

	void setTelemetry(boolean telemetry);
}
