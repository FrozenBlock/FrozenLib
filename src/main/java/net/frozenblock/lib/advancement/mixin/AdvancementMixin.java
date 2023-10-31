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

import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.advancement.impl.AdvancementInteraction;
import net.frozenblock.lib.advancement.impl.DisplayInfoInteraction;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import java.util.Map;
import java.util.Optional;

@Mixin(Advancement.class)
public abstract class AdvancementMixin implements AdvancementInteraction {
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

	@Shadow
	public abstract Optional<DisplayInfo> display();

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
	public Optional<Component> frozenLib$title() {
		return this.display().isPresent() ? Optional.of(this.display().get().getTitle()) : Optional.empty();
	}

	@Override
	public Optional<Component> frozenLib$description() {
		return this.display().isPresent() ? Optional.of(this.display().get().getDescription()) : Optional.empty();
	}

	@Override
	public Optional<ItemStack> frozenLib$icon() {
		return this.display().isPresent() ? Optional.of(this.display().get().getIcon()) : Optional.empty();
	}

	@Override
	public Optional<ResourceLocation> frozenLib$background() {
		return this.display().isPresent() ? Optional.of(this.display().get().getBackground()) : Optional.empty();
	}

	@Override
	public Optional<FrameType> frozenLib$frame() {
		return this.display().isPresent() ? Optional.of(this.display().get().getFrame()) : Optional.empty();
	}

	@Override
	public Optional<Boolean> frozenLib$showToast() {
		return this.display().isPresent() ? Optional.of(this.display().get().shouldShowToast()) : Optional.empty();
	}

	@Override
	public Optional<Boolean> frozenLib$announceChat() {
		return this.display().isPresent() ? Optional.of(this.display().get().shouldAnnounceChat()) : Optional.empty();
	}

	@Override
	public Optional<Boolean> frozenLib$hidden() {
		return this.display().isPresent() ? Optional.of(this.display().get().isHidden()) : Optional.empty();
	}

	@Override
	public Optional<Float> frozenLib$x() {
		return this.display().isPresent() ? Optional.of(this.display().get().getX()) : Optional.empty();
	}

	@Override
	public Optional<Float> frozenLib$y() {
		return this.display().isPresent() ? Optional.of(this.display().get().getY()) : Optional.empty();
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

	@Override
	public void frozenLib$setTitle(Component title) {
		if (this.display().isPresent()) {
			DisplayInfoInteraction.class.cast(this.display().get()).frozenLib$setTitle(title);
		}
	}

	@Override
	public void frozenLib$setDescription(Component description) {
		if (this.display().isPresent()) {
			DisplayInfoInteraction.class.cast(this.display().get()).frozenLib$setDescription(description);
		}
	}

	@Override
	public void frozenLib$setIcon(ItemStack icon) {
		if (this.display().isPresent()) {
			DisplayInfoInteraction.class.cast(this.display().get()).frozenLib$setIcon(icon);
		}
	}

	@Override
	public void frozenLib$setBackground(@Nullable ResourceLocation background) {
		if (this.display().isPresent()) {
			DisplayInfoInteraction.class.cast(this.display().get()).frozenLib$setBackground(background);
		}
	}

	@Override
	public void frozenLib$setFrame(FrameType frame) {
		if (this.display().isPresent()) {
			DisplayInfoInteraction.class.cast(this.display().get()).frozenLib$setFrame(frame);
		}
	}

	@Override
	public void frozenLib$setShowsToast(boolean showToast) {
		if (this.display().isPresent()) {
			DisplayInfoInteraction.class.cast(this.display().get()).frozenLib$setShowsToast(showToast);
		}
	}

	@Override
	public void frozenLib$setAnnounceChat(boolean announceChat) {
		if (this.display().isPresent()) {
			DisplayInfoInteraction.class.cast(this.display().get()).frozenLib$setAnnounceChat(announceChat);
		}
	}

	@Override
	public void frozenLib$setHidden(boolean hidden) {
		if (this.display().isPresent()) {
			DisplayInfoInteraction.class.cast(this.display().get()).frozenLib$setHidden(hidden);
		}
	}

	@Override
	public void frozenLib$setX(float x) {
		if (this.display().isPresent()) {
			DisplayInfoInteraction.class.cast(this.display().get()).frozenLib$setX(x);
		}
	}

	@Override
	public void frozenLib$setY(float y) {
		if (this.display().isPresent()) {
			DisplayInfoInteraction.class.cast(this.display().get()).frozenLib$setY(y);
		}
	}
}
