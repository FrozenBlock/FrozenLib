package net.frozenblock.lib.transfer.api;

import net.minecraft.world.item.ItemStack;

public interface ItemHandler {

	int size();

	ItemStack getStack(int index);

	int getCapacity(int index, ItemStack stack);

	boolean isValid(int index, ItemStack stack);

	int insert(int index, ItemStack stack, boolean simulate);

	ItemStack extract(int index, int amount, boolean simulate);
}
