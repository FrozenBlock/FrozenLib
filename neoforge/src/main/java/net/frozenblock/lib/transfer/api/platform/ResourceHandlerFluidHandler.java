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
import net.frozenblock.lib.transfer.api.FluidTransferVariant;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public final class ResourceHandlerFluidHandler implements FluidHandler {
	private final ResourceHandler<FluidResource> handler;

	public ResourceHandlerFluidHandler(ResourceHandler<FluidResource> handler) {
		this.handler = handler;
	}

	@Override
	public int size() {
		return this.handler.size();
	}

	@Override
	public FluidTransferVariant getVariant(int index) {
		FluidResource resource = this.handler.getResource(index);
		return resource.isEmpty() ? FluidTransferVariant.BLANK : TransferApiImpl.fromNeoForge(resource);
	}

	@Override
	public int getAmount(int index) {
		return this.handler.getAmountAsInt(index);
	}

	@Override
	public int getCapacity(int index, FluidTransferVariant variant) {
		return this.handler.getCapacityAsInt(index, TransferApiImpl.toNeoForge(variant));
	}

	@Override
	public boolean isValid(int index, FluidTransferVariant variant) {
		if (variant.isBlank()) return false;
		return this.handler.isValid(index, TransferApiImpl.toNeoForge(variant));
	}

	@Override
	public void setStack(int index, FluidTransferVariant variant, int amount) {
		try (Transaction transaction = Transaction.open(null)) {
			FluidResource current = this.handler.getResource(index);
			if (!current.isEmpty()) this.handler.extract(index, current, this.handler.getAmountAsInt(index), transaction);
			if (!variant.isBlank() && amount > 0) this.handler.insert(index, TransferApiImpl.toNeoForge(variant), amount, transaction);
			transaction.commit();
		}
	}

	@Override
	public int insert(int index, FluidTransferVariant variant, int amount, boolean simulate) {
		if (variant.isBlank() || amount <= 0) return 0;

		FluidResource resource = TransferApiImpl.toNeoForge(variant);
		try (Transaction transaction = Transaction.open(null)) {
			int inserted = this.handler.insert(index, resource, amount, transaction);
			if (!simulate) transaction.commit();
			return inserted;
		}
	}

	@Override
	public int extract(int index, FluidTransferVariant variant, int amount, boolean simulate) {
		if (variant.isBlank() || amount <= 0) return 0;

		FluidResource resource = TransferApiImpl.toNeoForge(variant);
		try (Transaction transaction = Transaction.open(null)) {
			int extracted = this.handler.extract(index, resource, amount, transaction);
			if (!simulate) transaction.commit();
			return extracted;
		}
	}
}
