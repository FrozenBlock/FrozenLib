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

import net.minecraft.advancements.FrameType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface DisplayInfoInteraction {

	Component frozenLib$title();

	Component frozenLib$description();

	ItemStack frozenLib$icon();

	ResourceLocation frozenLib$background();

	FrameType frozenLib$frame();

	boolean frozenLib$showToast();

	boolean frozenLib$announceChat();

	boolean frozenLib$hidden();

	float frozenLib$x();

	float frozenLib$y();

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
