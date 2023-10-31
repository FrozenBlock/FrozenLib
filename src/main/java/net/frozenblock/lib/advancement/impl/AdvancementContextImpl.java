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
import net.minecraft.advancements.FrameType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

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
	public Optional<Component> title() {
		return (AdvancementInteraction.class.cast(holder().value())).frozenLib$title();
	}

	@Override
	public Optional<Component> description() {
		return (AdvancementInteraction.class.cast(holder().value())).frozenLib$description();
	}

	@Override
	public Optional<ItemStack> icon() {
		return (AdvancementInteraction.class.cast(holder().value())).frozenLib$icon();
	}

	@Override
	public Optional<ResourceLocation> background() {
		return (AdvancementInteraction.class.cast(holder().value())).frozenLib$background();
	}

	@Override
	public Optional<FrameType> frame() {
		return (AdvancementInteraction.class.cast(holder().value())).frozenLib$frame();
	}

	@Override
	public Optional<Boolean> showToast() {
		return (AdvancementInteraction.class.cast(holder().value())).frozenLib$showToast();
	}

	@Override
	public Optional<Boolean> announceChat() {
		return (AdvancementInteraction.class.cast(holder().value())).frozenLib$announceChat();
	}

	@Override
	public Optional<Boolean> hidden() {
		return (AdvancementInteraction.class.cast(holder().value())).frozenLib$hidden();
	}

	@Override
	public Optional<Float> x() {
		return (AdvancementInteraction.class.cast(holder().value())).frozenLib$x();
	}

	@Override
	public Optional<Float> y() {
		return (AdvancementInteraction.class.cast(holder().value())).frozenLib$y();
	}

	@Override
	public void setParent(Optional<ResourceLocation> parentLocation) {
		(AdvancementInteraction.class.cast(holder().value())).frozenLib$setParent(parentLocation);
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

	@Override
	public void setTitle(Component title) {
		(AdvancementInteraction.class.cast(holder().value())).frozenLib$setTitle(title);
	}

	@Override
	public void setDescription(Component description) {
		(AdvancementInteraction.class.cast(holder().value())).frozenLib$setDescription(description);
	}

	@Override
	public void setIcon(ItemStack icon) {
		(AdvancementInteraction.class.cast(holder().value())).frozenLib$setIcon(icon);
	}

	@Override
	public void setBackground(@Nullable ResourceLocation background) {
		(AdvancementInteraction.class.cast(holder().value())).frozenLib$setBackground(background);
	}

	@Override
	public void setFrame(FrameType frame) {
		(AdvancementInteraction.class.cast(holder().value())).frozenLib$setFrame(frame);
	}

	@Override
	public void setShowsToast(boolean showToast) {
		(AdvancementInteraction.class.cast(holder().value())).frozenLib$setShowsToast(showToast);
	}

	@Override
	public void setAnnounceChat(boolean announceChat) {
		(AdvancementInteraction.class.cast(holder().value())).frozenLib$setAnnounceChat(announceChat);
	}

	@Override
	public void setHidden(boolean hidden) {
		(AdvancementInteraction.class.cast(holder().value())).frozenLib$setHidden(hidden);
	}

	@Override
	public void setX(float x) {
		(AdvancementInteraction.class.cast(holder().value())).frozenLib$setX(x);
	}

	@Override
	public void setY(float y) {
		(AdvancementInteraction.class.cast(holder().value())).frozenLib$setY(y);
	}
}
