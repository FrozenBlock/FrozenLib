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

import net.frozenblock.lib.transfer.api.ItemHandler;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public final class ResourceHandlerItemHandler implements ItemHandler {
	private final ResourceHandler<ItemResource> handler;

	public ResourceHandlerItemHandler(ResourceHandler<ItemResource> handler) {
		this.handler = handler;
	}

	@Override
	public int size() {
		return this.handler.size();
	}

	@Override
	public ItemStack getStack(int index) {
		ItemResource resource = this.handler.getResource(index);
		return resource.isEmpty() ? ItemStack.EMPTY : resource.toStack(this.handler.getAmountAsInt(index));
	}

	@Override
	public int getCapacity(int index, ItemStack stack) {
		return this.handler.getCapacityAsInt(index, ItemResource.of(stack));
	}

	@Override
	public boolean isValid(int index, ItemStack stack) {
		if (stack.isEmpty()) return false;
		return this.handler.isValid(index, ItemResource.of(stack));
	}

	@Override
	public int insert(int index, ItemStack stack, boolean simulate) {
		if (stack.isEmpty()) return 0;

		ItemResource resource = ItemResource.of(stack);
		try (Transaction transaction = Transaction.open(null)) {
			int inserted = this.handler.insert(index, resource, stack.getCount(), transaction);
			if (!simulate) transaction.commit();
			return inserted;
		}
	}

	@Override
	public ItemStack extract(int index, int amount, boolean simulate) {
		if (amount <= 0) return ItemStack.EMPTY;

		ItemResource resource = this.handler.getResource(index);
		if (resource.isEmpty()) return ItemStack.EMPTY;

		try (Transaction transaction = Transaction.open(null)) {
			int extracted = this.handler.extract(index, resource, amount, transaction);
			if (extracted <= 0) return ItemStack.EMPTY;

			if (!simulate) transaction.commit();
			return resource.toStack(extracted);
		}
	}
}
