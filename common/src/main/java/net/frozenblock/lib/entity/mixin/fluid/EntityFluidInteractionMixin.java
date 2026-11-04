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

package net.frozenblock.lib.entity.mixin.fluid;

import net.frozenblock.lib.entity.impl.fluid.EntityFluidInteractionInterface;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityFluidInteraction;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import java.util.List;

@Mixin(EntityFluidInteraction.class)
public class EntityFluidInteractionMixin implements EntityFluidInteractionInterface {

	@Shadow
	@Final
	private List<EntityFluidInteraction.Tracker> fluidTrackers;

	@Unique
	@Override
	public boolean frozenLib$wasInFluid(ResourceKey<Fluid> fluid) {
		for (EntityFluidInteraction.Tracker tracker : this.fluidTrackers) {
			if (tracker.wasInFluid && tracker.fluidType.is(fluid)) return true;
		}

		return false;
	}

	@Unique
	@Override
	public boolean frozenLib$wasInFluid(TagKey<Fluid> fluid) {
		for (EntityFluidInteraction.Tracker tracker : this.fluidTrackers) {
			if (tracker.wasInFluid && tracker.fluidType.is(fluid)) return true;
		}

		return false;
	}
}
