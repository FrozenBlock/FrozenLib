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

import net.frozenblock.lib.advancement.impl.DisplayInfoInteraction;
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

@Mixin(DisplayInfo.class)
public abstract class DisplayInfoMixin implements DisplayInfoInteraction {
	@Mutable
	@Final
	@Shadow
	private Component title;

	@Mutable
	@Shadow
	@Final
	private Component description;

	@Mutable
	@Shadow
	@Final
	private ItemStack icon;

	@Mutable
	@Shadow
	@Nullable
	@Final
	private ResourceLocation background;

	@Mutable
	@Shadow
	@Final
	private FrameType frame;

	@Mutable
	@Shadow
	@Final
	private boolean showToast;

	@Mutable
	@Shadow
	@Final
	private boolean announceChat;

	@Mutable
	@Shadow
	@Final
	private boolean hidden;

	@Mutable
	@Shadow
	private float x;

	@Mutable
	@Shadow
	private float y;

	@Shadow
	public abstract ItemStack getIcon();

	@Shadow
	@Nullable
	public abstract ResourceLocation getBackground();

	@Shadow
	public abstract Component getTitle();

	@Shadow
	public abstract Component getDescription();

	@Shadow
	public abstract FrameType getFrame();

	@Shadow
	public abstract boolean shouldShowToast();

	@Shadow
	public abstract boolean shouldAnnounceChat();

	@Shadow
	public abstract boolean isHidden();

	@Shadow
	public abstract float getX();

	@Shadow
	public abstract float getY();

	@Override
	public Component frozenLib$title() {
		return this.getTitle();
	}

	@Override
	public Component frozenLib$description() {
		return this.getDescription();
	}

	@Override
	public ItemStack frozenLib$icon() {
		return this.getIcon();
	}

	@Override
	public ResourceLocation frozenLib$background() {
		return this.getBackground();
	}

	@Override
	public FrameType frozenLib$frame() {
		return this.getFrame();
	}

	@Override
	public boolean frozenLib$showToast() {
		return this.shouldShowToast();
	}

	@Override
	public boolean frozenLib$announceChat() {
		return this.shouldAnnounceChat();
	}

	@Override
	public boolean frozenLib$hidden() {
		return this.isHidden();
	}

	@Override
	public float frozenLib$x() {
		return this.getX();
	}

	@Override
	public float frozenLib$y() {
		return this.getY();
	}

	@Override
	public void frozenLib$setTitle(Component title) {
		this.title = title;
	}

	@Override
	public void frozenLib$setDescription(Component description) {
		this.description = description;
	}

	@Override
	public void frozenLib$setIcon(ItemStack icon) {
		this.icon = icon;
	}

	@Override
	public void frozenLib$setBackground(@Nullable ResourceLocation background) {
		this.background = background;
	}

	@Override
	public void frozenLib$setFrame(FrameType frame) {
		this.frame = frame;
	}

	@Override
	public void frozenLib$setShowsToast(boolean showToast) {
		this.showToast = showToast;
	}

	@Override
	public void frozenLib$setAnnounceChat(boolean announceChat) {
		this.announceChat = announceChat;
	}

	@Override
	public void frozenLib$setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	@Override
	public void frozenLib$setX(float x) {
		this.x = x;
	}

	@Override
	public void frozenLib$setY(float y) {
		this.y = y;
	}
}
