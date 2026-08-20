package net.frozenblock.lib.transfer.api.platform;

import net.frozenblock.lib.transfer.api.FluidHandler;
import net.frozenblock.lib.transfer.api.FluidVariant;
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
	public FluidVariant getVariant(int index) {
		FluidResource resource = this.handler.getResource(index);
		return resource.isEmpty() ? FluidVariant.BLANK : TransferApiImpl.fromNeoForge(resource);
	}

	@Override
	public int getAmount(int index) {
		return this.handler.getAmountAsInt(index);
	}

	@Override
	public int getCapacity(int index, FluidVariant variant) {
		return this.handler.getCapacityAsInt(index, TransferApiImpl.toNeoForge(variant));
	}

	@Override
	public boolean isValid(int index, FluidVariant variant) {
		if (variant.isBlank()) return false;
		return this.handler.isValid(index, TransferApiImpl.toNeoForge(variant));
	}

	@Override
	public void setStack(int index, FluidVariant variant, int amount) {
		try (Transaction transaction = Transaction.open(null)) {
			FluidResource current = this.handler.getResource(index);
			if (!current.isEmpty()) this.handler.extract(index, current, this.handler.getAmountAsInt(index), transaction);
			if (!variant.isBlank() && amount > 0) this.handler.insert(index, TransferApiImpl.toNeoForge(variant), amount, transaction);
			transaction.commit();
		}
	}

	@Override
	public int insert(int index, FluidVariant variant, int amount, boolean simulate) {
		if (variant.isBlank() || amount <= 0) return 0;

		FluidResource resource = TransferApiImpl.toNeoForge(variant);
		try (Transaction transaction = Transaction.open(null)) {
			int inserted = this.handler.insert(index, resource, amount, transaction);
			if (!simulate) transaction.commit();
			return inserted;
		}
	}

	@Override
	public int extract(int index, FluidVariant variant, int amount, boolean simulate) {
		if (variant.isBlank() || amount <= 0) return 0;

		FluidResource resource = TransferApiImpl.toNeoForge(variant);
		try (Transaction transaction = Transaction.open(null)) {
			int extracted = this.handler.extract(index, resource, amount, transaction);
			if (!simulate) transaction.commit();
			return extracted;
		}
	}
}
