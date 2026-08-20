package net.frozenblock.lib.transfer.api.platform;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.frozenblock.lib.transfer.api.FrozenFluidHandler;
import net.frozenblock.lib.transfer.api.FrozenFluidVariant;

public final class FluidHandlerStorage implements SlottedStorage<FluidVariant> {
	private final List<SingleSlotStorage<FluidVariant>> slots;

	public FluidHandlerStorage(FrozenFluidHandler handler) {
		int size = handler.size();
		List<SingleSlotStorage<FluidVariant>> slots = new ArrayList<>(size);
		for (int index = 0; index < size; index++) slots.add(new Slot(handler, index));
		this.slots = List.copyOf(slots);
	}

	@Override
	public int getSlotCount() {
		return this.slots.size();
	}

	@Override
	public SingleSlotStorage<FluidVariant> getSlot(int slot) {
		return this.slots.get(slot);
	}

	@Override
	public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
		long inserted = 0;
		for (SingleSlotStorage<FluidVariant> slot : this.slots) {
			inserted += slot.insert(resource, maxAmount - inserted, transaction);
			if (inserted == maxAmount) break;
		}
		return inserted;
	}

	@Override
	public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
		long extracted = 0;
		for (SingleSlotStorage<FluidVariant> slot : this.slots) {
			extracted += slot.extract(resource, maxAmount - extracted, transaction);
			if (extracted == maxAmount) break;
		}
		return extracted;
	}

	@Override
	public Iterator<StorageView<FluidVariant>> iterator() {
		return new ArrayList<StorageView<FluidVariant>>(this.slots).iterator();
	}

	private static final class Slot extends SingleVariantStorage<FluidVariant> {
		private final FrozenFluidHandler handler;
		private final int index;

		Slot(FrozenFluidHandler handler, int index) {
			this.handler = handler;
			this.index = index;

			FrozenFluidVariant variant = handler.getVariant(index);
			if (!variant.isBlank()) {
				this.variant = TransferApiImpl.toFabric(variant);
				this.amount = TransferApiImpl.millibucketsToDroplets(handler.getAmount(index));
			}
		}

		@Override
		protected FluidVariant getBlankVariant() {
			return FluidVariant.blank();
		}

		@Override
		protected long getCapacity(FluidVariant variant) {
			FrozenFluidVariant frozenVariant = variant.isBlank() ? FrozenFluidVariant.BLANK : TransferApiImpl.fromFabric(variant);
			return TransferApiImpl.millibucketsToDroplets(this.handler.getCapacity(this.index, frozenVariant));
		}

		@Override
		protected boolean canInsert(FluidVariant variant) {
			return this.handler.isValid(this.index, TransferApiImpl.fromFabric(variant));
		}

		@Override
		protected void onFinalCommit() {
			FrozenFluidVariant frozenVariant = this.variant.isBlank() ? FrozenFluidVariant.BLANK : TransferApiImpl.fromFabric(this.variant);
			this.handler.setStack(this.index, frozenVariant, TransferApiImpl.dropletsToMillibuckets(this.amount));
		}
	}
}
