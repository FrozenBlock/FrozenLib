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

import net.frozenblock.lib.transfer.api.FluidHandler;
import net.frozenblock.lib.transfer.api.FluidStack;
import net.frozenblock.lib.transfer.api.FluidVariant;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public final class FluidHandlerResourceHandler implements ResourceHandler<FluidResource> {
	private final FluidHandler handler;
	private final IndexJournal[] journals;

	public FluidHandlerResourceHandler(FluidHandler handler) {
		this.handler = handler;
		int size = handler.size();
		this.journals = new IndexJournal[size];
		for (int index = 0; index < size; index++) this.journals[index] = new IndexJournal(index);
	}

	@Override
	public int size() {
		return this.handler.size();
	}

	@Override
	public FluidResource getResource(int index) {
		return TransferApiImpl.toNeoForge(this.handler.getVariant(index));
	}

	@Override
	public long getAmountAsLong(int index) {
		return this.handler.getAmount(index);
	}

	@Override
	public long getCapacityAsLong(int index, FluidResource resource) {
		return this.handler.getCapacity(index, resource.isEmpty() ? FluidVariant.BLANK : TransferApiImpl.fromNeoForge(resource));
	}

	@Override
	public boolean isValid(int index, FluidResource resource) {
		return this.handler.isValid(index, TransferApiImpl.fromNeoForge(resource));
	}

	@Override
	public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
		TransferPreconditions.checkNonEmptyNonNegative(resource, amount);

		FluidVariant variant = TransferApiImpl.fromNeoForge(resource);
		FluidVariant current = this.handler.getVariant(index);
		if (!current.isBlank() && !current.equals(variant)) return 0;
		if (!this.handler.isValid(index, variant)) return 0;

		int currentAmount = this.handler.getAmount(index);
		int inserted = Math.min(amount, this.handler.getCapacity(index, variant) - currentAmount);
		if (inserted <= 0) return 0;

		this.journals[index].updateSnapshots(transaction);
		this.handler.setStack(index, variant, currentAmount + inserted);
		return inserted;
	}

	@Override
	public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
		TransferPreconditions.checkNonEmptyNonNegative(resource, amount);

		FluidVariant variant = TransferApiImpl.fromNeoForge(resource);
		FluidVariant current = this.handler.getVariant(index);
		if (!current.equals(variant)) return 0;

		int currentAmount = this.handler.getAmount(index);
		int extracted = Math.min(amount, currentAmount);
		if (extracted <= 0) return 0;

		this.journals[index].updateSnapshots(transaction);
		int remaining = currentAmount - extracted;
		this.handler.setStack(index, remaining == 0 ? FluidVariant.BLANK : variant, remaining);
		return extracted;
	}

	private final class IndexJournal extends SnapshotJournal<FluidStack> {
		private final int index;

		private IndexJournal(int index) {
			this.index = index;
		}

		@Override
		protected FluidStack createSnapshot() {
			return new FluidStack(FluidHandlerResourceHandler.this.handler.getVariant(this.index), FluidHandlerResourceHandler.this.handler.getAmount(this.index));
		}

		@Override
		protected void revertToSnapshot(FluidStack snapshot) {
			FluidHandlerResourceHandler.this.handler.setStack(this.index, snapshot.variant(), snapshot.amount());
		}
	}
}
