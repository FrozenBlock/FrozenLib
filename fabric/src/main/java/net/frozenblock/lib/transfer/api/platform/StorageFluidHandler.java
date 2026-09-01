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

package net.frozenblock.lib.transfer.api.platform;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.frozenblock.lib.transfer.api.FluidHandler;
import net.frozenblock.lib.transfer.api.FluidTransferVariant;
import org.jetbrains.annotations.Nullable;

public final class StorageFluidHandler implements FluidHandler {
	private final Storage<FluidVariant> storage;
	private final @Nullable SlottedStorage<FluidVariant> slotted;

	public StorageFluidHandler(Storage<FluidVariant> storage) {
		this.storage = storage;
		this.slotted = storage instanceof SlottedStorage<FluidVariant> slotted ? slotted : null;
	}

	private SingleSlotStorage<FluidVariant> slot(int index) {
		return this.slotted.getSlot(index);
	}

	private @Nullable StorageView<FluidVariant> firstView() {
		for (StorageView<FluidVariant> view : this.storage.nonEmptyViews()) return view;
		return null;
	}

	@Override
	public int size() {
		return this.slotted != null ? this.slotted.getSlotCount() : 1;
	}

	@Override
	public FluidTransferVariant getVariant(int index) {
		if (this.slotted != null) return TransferApiImpl.fromFabric(slot(index).getResource());

		final StorageView<FluidVariant> view = firstView();
		return view == null ? FluidTransferVariant.BLANK : TransferApiImpl.fromFabric(view.getResource());
	}

	@Override
	public int getAmount(int index) {
		if (this.slotted != null) return TransferApiImpl.dropletsToMillibuckets(slot(index).getAmount());

		final StorageView<FluidVariant> view = firstView();
		return view == null ? 0 : TransferApiImpl.dropletsToMillibuckets(view.getAmount());
	}

	@Override
	public int getCapacity(int index, FluidTransferVariant variant) {
		if (this.slotted != null) return TransferApiImpl.dropletsToMillibuckets(slot(index).getCapacity());
		if (variant.isBlank()) return 0;

		final long current = TransferApiImpl.millibucketsToDroplets(getAmount(index));
		final long insertable = StorageUtil.simulateInsert(this.storage, TransferApiImpl.toFabric(variant), Long.MAX_VALUE, null);
		return TransferApiImpl.dropletsToMillibuckets(current + insertable);
	}

	@Override
	public boolean isValid(int index, FluidTransferVariant variant) {
		if (variant.isBlank()) return false;

		final Storage<FluidVariant> target = this.slotted != null ? slot(index) : this.storage;
		return StorageUtil.simulateInsert(target, TransferApiImpl.toFabric(variant), 1, null) > 0;
	}

	@Override
	public void setStack(int index, FluidTransferVariant variant, int amount) {
		Storage<FluidVariant> target;
		FluidVariant current;
		long currentAmount;
		if (this.slotted != null) {
			final SingleSlotStorage<FluidVariant> slot = slot(index);
			target = slot;
			current = slot.getResource();
			currentAmount = slot.getAmount();
		} else {
			final StorageView<FluidVariant> view = firstView();
			target = this.storage;
			current = view == null ? FluidVariant.blank() : view.getResource();
			currentAmount = view == null ? 0 : view.getAmount();
		}

		try (Transaction transaction = Transaction.openOuter()) {
			if (!current.isBlank()) target.extract(current, currentAmount, transaction);
			if (!variant.isBlank() && amount > 0) {
				target.insert(TransferApiImpl.toFabric(variant), TransferApiImpl.millibucketsToDroplets(amount), transaction);
			}
			transaction.commit();
		}
	}

	@Override
	public int insert(int index, FluidTransferVariant variant, int amount, boolean simulate) {
		if (variant.isBlank() || amount <= 0) return 0;

		final Storage<FluidVariant> target = this.slotted != null ? slot(index) : this.storage;
		final FluidVariant fabricVariant = TransferApiImpl.toFabric(variant);
		final long maxDroplets = TransferApiImpl.millibucketsToDroplets(amount);

		try (Transaction transaction = Transaction.openOuter()) {
			final long inserted = target.insert(fabricVariant, maxDroplets, transaction);
			if (!simulate) transaction.commit();
			return TransferApiImpl.dropletsToMillibuckets(inserted);
		}
	}

	@Override
	public int extract(int index, FluidTransferVariant variant, int amount, boolean simulate) {
		if (variant.isBlank() || amount <= 0) return 0;

		final Storage<FluidVariant> target = this.slotted != null ? slot(index) : this.storage;
		final FluidVariant fabricVariant = TransferApiImpl.toFabric(variant);
		final long maxDroplets = TransferApiImpl.millibucketsToDroplets(amount);

		try (Transaction transaction = Transaction.openOuter()) {
			final long extracted = target.extract(fabricVariant, maxDroplets, transaction);
			if (!simulate) transaction.commit();
			return TransferApiImpl.dropletsToMillibuckets(extracted);
		}
	}
}
