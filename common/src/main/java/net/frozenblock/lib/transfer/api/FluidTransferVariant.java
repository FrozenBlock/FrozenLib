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

package net.frozenblock.lib.transfer.api;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public record FluidTransferVariant(Fluid fluid, DataComponentPatch components) {

	public static final FluidTransferVariant BLANK = new FluidTransferVariant(Fluids.EMPTY, DataComponentPatch.EMPTY);

	public static FluidTransferVariant of(Fluid fluid) {
		return of(fluid, DataComponentPatch.EMPTY);
	}

	public static FluidTransferVariant of(Fluid fluid, DataComponentPatch components) {
		return new FluidTransferVariant(fluid, components);
	}

	public boolean isBlank() {
		return this.fluid == Fluids.EMPTY;
	}
}
