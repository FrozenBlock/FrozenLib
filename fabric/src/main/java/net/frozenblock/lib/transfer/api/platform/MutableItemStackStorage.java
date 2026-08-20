package net.frozenblock.lib.transfer.api.platform;

import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.minecraft.world.item.ItemStack;

public final class MutableItemStackStorage extends SingleStackStorage {
	private ItemStack stack;

	public MutableItemStackStorage(ItemStack stack) {
		this.stack = stack;
	}

	@Override
	protected ItemStack getStack() {
		return this.stack;
	}

	@Override
	protected void setStack(ItemStack stack) {
		this.stack = stack;
	}

	ItemStack currentStack() {
		return this.stack;
	}
}
