package net.frozenblock.lib.transfer.api.platform;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.frozenblock.lib.transfer.api.FrozenFluidContainerItem;
import net.frozenblock.lib.transfer.api.FrozenFluidVariant;
import net.minecraft.world.item.ItemStack;

public final class ContainerItemFluidHandler implements FrozenFluidContainerItem {
	private final MutableItemStackStorage stackStorage;
	private final StorageFluidHandler delegate;

	public ContainerItemFluidHandler(MutableItemStackStorage stackStorage, Storage<FluidVariant> fluidStorage) {
		this.stackStorage = stackStorage;
		this.delegate = new StorageFluidHandler(fluidStorage);
	}

	@Override
	public ItemStack getStack() {
		return this.stackStorage.currentStack();
	}

	@Override
	public int size() {
		return this.delegate.size();
	}

	@Override
	public FrozenFluidVariant getVariant(int index) {
		return this.delegate.getVariant(index);
	}

	@Override
	public int getAmount(int index) {
		return this.delegate.getAmount(index);
	}

	@Override
	public int getCapacity(int index, FrozenFluidVariant variant) {
		return this.delegate.getCapacity(index, variant);
	}

	@Override
	public boolean isValid(int index, FrozenFluidVariant variant) {
		return this.delegate.isValid(index, variant);
	}

	@Override
	public void setStack(int index, FrozenFluidVariant variant, int amount) {
		this.delegate.setStack(index, variant, amount);
	}

	@Override
	public int insert(int index, FrozenFluidVariant variant, int amount, boolean simulate) {
		return this.delegate.insert(index, variant, amount, simulate);
	}

	@Override
	public int extract(int index, FrozenFluidVariant variant, int amount, boolean simulate) {
		return this.delegate.extract(index, variant, amount, simulate);
	}
}
