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
