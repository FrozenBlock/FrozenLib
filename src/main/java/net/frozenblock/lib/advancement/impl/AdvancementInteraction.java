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

import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
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

	Optional<Component> frozenLib$title();

	Optional<Component> frozenLib$description();

	Optional<ItemStack> frozenLib$icon();

	Optional<ResourceLocation> frozenLib$background();

	Optional<FrameType> frozenLib$frame();

	Optional<Boolean> frozenLib$showToast();

	Optional<Boolean> frozenLib$announceChat();

	Optional<Boolean> frozenLib$hidden();

	Optional<Float> frozenLib$x();

	Optional<Float> frozenLib$y();

	void frozenLib$setParent(Optional<ResourceLocation> parent);

	void frozenLib$setDisplay(Optional<DisplayInfo> display);

	void frozenLib$setRewards(AdvancementRewards rewards);

	void frozenLib$setCriteria(Map<String, Criterion<?>> criteria);

	void frozenLib$setRequirements(AdvancementRequirements requirements);

	void frozenLib$setSendsTelemetryEvent(boolean sendsTelemetryEvent);

	void frozenLib$setName(Optional<Component> name);

	void frozenLib$setTitle(Component title);

	void frozenLib$setDescription(Component description);

	void frozenLib$setIcon(ItemStack icon);

	void frozenLib$setBackground(@Nullable ResourceLocation background);

	void frozenLib$setFrame(FrameType frame);

	void frozenLib$setShowsToast(boolean showToast);

	void frozenLib$setAnnounceChat(boolean announceChat);

	void frozenLib$setHidden(boolean hidden);

	void frozenLib$setX(float x);

	void frozenLib$setY(float y);
}
