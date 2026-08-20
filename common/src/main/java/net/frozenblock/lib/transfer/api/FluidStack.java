package net.frozenblock.lib.transfer.api;

public record FluidStack(FluidVariant variant, int amount) {

	public static final FluidStack EMPTY = new FluidStack(FluidVariant.BLANK, 0);

	public boolean isEmpty() {
		return this.amount <= 0 || this.variant.isBlank();
	}
}
