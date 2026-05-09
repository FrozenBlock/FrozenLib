/*
 * Copyright (C) 2026 FrozenBlock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.block.mixin.clipgroup;

import it.unimi.dsi.fastutil.objects.Reference2BooleanArrayMap;
import java.util.Map;
import net.frozenblock.lib.block.impl.clipgroup.ClipGroup;
import net.frozenblock.lib.block.impl.clipgroup.ClipGroupInterface;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Entity.class)
public class EntityMixin implements ClipGroupInterface {

	@Unique
	private final Map<ClipGroup, Boolean> frozenLib$clipGroupStatuses = new Reference2BooleanArrayMap<>();

	@Override
	public void frozenLib$setClipInGroup(ClipGroup group, boolean inside) {
		this.frozenLib$clipGroupStatuses.put(group, inside);
	}

	@Override
	public boolean frozenLib$wasClipInGroup(ClipGroup group) {
		return this.frozenLib$clipGroupStatuses.getOrDefault(group, false);
	}

	@Override
	public boolean frozenLib$wasClipInGroup(BlockState state) {
		return this.frozenLib$clipGroupStatuses.entrySet()
			.stream()
			.filter(entry -> entry.getKey().contains(state))
			.anyMatch(entry -> entry.getValue() == true);
	}
}
