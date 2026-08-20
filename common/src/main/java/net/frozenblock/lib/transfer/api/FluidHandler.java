package net.frozenblock.lib.transfer.api;

public interface FluidHandler {

	int size();

	FluidVariant getVariant(int index);

	int getAmount(int index);

	int getCapacity(int index, FluidVariant variant);

	boolean isValid(int index, FluidVariant variant);

	void setStack(int index, FluidVariant variant, int amount);

	default int insert(int index, FluidVariant variant, int amount, boolean simulate) {
		if (variant.isBlank() || amount <= 0) return 0;

		FluidVariant current = getVariant(index);
		if (!current.isBlank() && !current.equals(variant)) return 0;
		if (!isValid(index, variant)) return 0;

		int currentAmount = getAmount(index);
		int inserted = Math.min(amount, getCapacity(index, variant) - currentAmount);
		if (inserted <= 0) return 0;

		if (!simulate) setStack(index, variant, currentAmount + inserted);
		return inserted;
	}

	default int extract(int index, FluidVariant variant, int amount, boolean simulate) {
		if (variant.isBlank() || amount <= 0) return 0;

		FluidVariant current = getVariant(index);
		if (!current.equals(variant)) return 0;

		int currentAmount = getAmount(index);
		int extracted = Math.min(amount, currentAmount);
		if (extracted <= 0) return 0;

		if (!simulate) {
			int remaining = currentAmount - extracted;
			setStack(index, remaining == 0 ? FluidVariant.BLANK : variant, remaining);
		}
		return extracted;
	}
}
