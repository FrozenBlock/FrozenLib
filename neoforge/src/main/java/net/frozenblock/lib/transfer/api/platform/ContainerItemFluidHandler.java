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

import net.frozenblock.lib.transfer.api.FluidContainerItem;
import net.frozenblock.lib.transfer.api.FluidTransferVariant;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

public final class ContainerItemFluidHandler implements FluidContainerItem {
	private final SimpleContainer container;
	private final ResourceHandlerFluidHandler wrapped;

	public ContainerItemFluidHandler(SimpleContainer container, ResourceHandler<FluidResource> fluidHandler) {
		this.container = container;
		this.wrapped = new ResourceHandlerFluidHandler(fluidHandler);
	}

	@Override
	public ItemStack getStack() {
		return this.container.getItem(0);
	}

	@Override
	public int size() {
		return this.wrapped.size();
	}

	@Override
	public FluidTransferVariant getVariant(int index) {
		return this.wrapped.getVariant(index);
	}

	@Override
	public int getAmount(int index) {
		return this.wrapped.getAmount(index);
	}

	@Override
	public int getCapacity(int index, FluidTransferVariant variant) {
		return this.wrapped.getCapacity(index, variant);
	}

	@Override
	public boolean isValid(int index, FluidTransferVariant variant) {
		return this.wrapped.isValid(index, variant);
	}

	@Override
	public void setStack(int index, FluidTransferVariant variant, int amount) {
		this.wrapped.setStack(index, variant, amount);
	}

	@Override
	public int insert(int index, FluidTransferVariant variant, int amount, boolean simulate) {
		return this.wrapped.insert(index, variant, amount, simulate);
	}

	@Override
	public int extract(int index, FluidTransferVariant variant, int amount, boolean simulate) {
		return this.wrapped.extract(index, variant, amount, simulate);
	}
}
