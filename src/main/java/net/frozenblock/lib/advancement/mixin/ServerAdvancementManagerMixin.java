/*
 * Copyright 2023 FrozenBlock
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

package net.frozenblock.lib.advancement.mixin;

import com.google.gson.JsonElement;
import net.frozenblock.lib.advancement.api.AdvancementContext;
import net.frozenblock.lib.advancement.api.AdvancementEvents;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mixin(ServerAdvancementManager.class)
public class ServerAdvancementManagerMixin {

	@Shadow
	private Map<ResourceLocation, AdvancementHolder> advancements;

	@Inject(
		method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/advancements/AdvancementTree;<init>()V"
		)
	)
	private void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {

		for (AdvancementHolder holder : advancements.values()) {
			var advancement = holder.value();
			var context = new AdvancementContext() {
				@Override
				public ResourceLocation key() {
					return holder.id();
				}

				@Override
				public Optional<ResourceLocation> parent() {
					return advancement.parent();
				}

				@Override
				public Optional<DisplayInfo> display() {
					return advancement.display();
				}

				@Override
				public AdvancementRewards rewards() {
					return advancement.rewards();
				}

				@Override
				public Map<String, Criterion<?>> criteria() {
					return advancement.criteria();
				}

				@Override
				public AdvancementRequirements requirements() {
					return advancement.requirements();
				}

				@Override
				public boolean sendsTelemetryEvent() {
					return advancement.sendsTelemetryEvent();
				}

				@Override
				public Optional<Component> name() {
					return advancement.name();
				}

				@Override
				public void addCriteria(String key, Criterion<?> criteria) {
					if (!(advancement.criteria() instanceof HashMap<String, Criterion<?>>)) {
                        advancement.criteria = new HashMap<>(advancement.criteria());
					}
					advancement.criteria().putIfAbsent(key, criteria);
				}

				@Override
				public void addRequirements(AdvancementRequirements requirements) {
					List<String[]> list = new ArrayList<>();
					list.addAll(Arrays.stream(advancement.requirements.requirements).toList());
					list.addAll(Arrays.stream(requirements.requirements).toList());
					advancement.requirements.requirements = list.toArray(new String[][]{});
				}

				@Override
				public void addLoot(List<Item> loot) {
					var rewards = this.rewards();
					List<ResourceLocation> newLoot = new ArrayList<>(Arrays.stream(rewards.loot).toList());
					newLoot.addAll(loot.stream().map(BuiltInRegistries.ITEM::getKey).toList());
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
					advancement.sendsTelemetryEvent = telemetry;
				}
			};

			AdvancementEvents.INIT.invoker().onInit(context);
		}
	}
}
