package net.frozenblock.lib.transfer.api.platform;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.frozenblock.lib.transfer.api.FrozenItemHandler;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class StorageItemHandler implements FrozenItemHandler {
	private final Storage<ItemVariant> storage;
	private final @Nullable SlottedStorage<ItemVariant> slotted;

	public StorageItemHandler(Storage<ItemVariant> storage) {
		this.storage = storage;
		this.slotted = storage instanceof SlottedStorage<ItemVariant> slotted ? slotted : null;
	}

	private SingleSlotStorage<ItemVariant> slot(int index) {
		return this.slotted.getSlot(index);
	}

	private @Nullable StorageView<ItemVariant> firstView() {
		for (StorageView<ItemVariant> view : this.storage.nonEmptyViews()) return view;
		return null;
	}

	@Override
	public int size() {
		return this.slotted != null ? this.slotted.getSlotCount() : 1;
	}

	@Override
	public ItemStack getStack(int index) {
		if (this.slotted != null) {
			SingleSlotStorage<ItemVariant> slot = slot(index);
			return slot.getResource().toStack((int) slot.getAmount());
		}
		StorageView<ItemVariant> view = firstView();
		return view == null ? ItemStack.EMPTY : view.getResource().toStack((int) view.getAmount());
	}

	@Override
	public int getCapacity(int index, ItemStack stack) {
		if (stack.isEmpty()) return 0;
		if (this.slotted != null) return (int) slot(index).getCapacity();

		long current = getStack(index).getCount();
		long insertable = StorageUtil.simulateInsert(this.storage, ItemVariant.of(stack), Integer.MAX_VALUE, null);
		return (int) (current + insertable);
	}

	@Override
	public boolean isValid(int index, ItemStack stack) {
		if (stack.isEmpty()) return false;
		Storage<ItemVariant> target = this.slotted != null ? slot(index) : this.storage;
		return StorageUtil.simulateInsert(target, ItemVariant.of(stack), 1, null) > 0;
	}

	@Override
	public int insert(int index, ItemStack stack, boolean simulate) {
		if (stack.isEmpty()) return 0;

		Storage<ItemVariant> target = this.slotted != null ? slot(index) : this.storage;
		ItemVariant variant = ItemVariant.of(stack);

		try (Transaction transaction = Transaction.openOuter()) {
			int inserted = (int) target.insert(variant, stack.getCount(), transaction);
			if (!simulate) transaction.commit();
			return inserted;
		}
	}

	@Override
	public ItemStack extract(int index, int amount, boolean simulate) {
		if (amount <= 0) return ItemStack.EMPTY;

		Storage<ItemVariant> target;
		ItemVariant resource;
		if (this.slotted != null) {
			SingleSlotStorage<ItemVariant> slot = slot(index);
			target = slot;
			resource = slot.getResource();
		} else {
			StorageView<ItemVariant> view = firstView();
			if (view == null) return ItemStack.EMPTY;
			target = this.storage;
			resource = view.getResource();
		}
		if (resource.isBlank()) return ItemStack.EMPTY;

		try (Transaction transaction = Transaction.openOuter()) {
			int extracted = (int) target.extract(resource, amount, transaction);
			if (extracted <= 0) return ItemStack.EMPTY;

			if (!simulate) transaction.commit();
			return resource.toStack(extracted);
		}
	}
}
