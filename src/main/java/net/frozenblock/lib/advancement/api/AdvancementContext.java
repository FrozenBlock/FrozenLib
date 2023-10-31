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

	void setParent(Optional<ResourceLocation> parentLocation);

	void addCriteria(String key, Criterion<?> criteria);

	void addRequirements(AdvancementRequirements requirements);

	void addLootTables(List<ResourceLocation> lootTables);

	void addRecipes(Collection<ResourceLocation> recipes);

	void setExperience(int experience);

	void setTelemetry(boolean telemetry);
}
