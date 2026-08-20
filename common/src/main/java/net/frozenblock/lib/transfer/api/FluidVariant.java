package net.frozenblock.lib.transfer.api;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public record FluidVariant(Fluid fluid, DataComponentPatch components) {

	public static final FluidVariant BLANK = new FluidVariant(Fluids.EMPTY, DataComponentPatch.EMPTY);

	public static FluidVariant of(Fluid fluid) {
		return of(fluid, DataComponentPatch.EMPTY);
	}

	public static FluidVariant of(Fluid fluid, DataComponentPatch components) {
		return new FluidVariant(fluid, components);
	}

	public boolean isBlank() {
		return this.fluid == Fluids.EMPTY;
	}
}
