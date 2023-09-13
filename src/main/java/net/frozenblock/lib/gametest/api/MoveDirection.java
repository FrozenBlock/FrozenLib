/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.gametest.api;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

public enum MoveDirection {
	IN((inventory, resource, maxAmount, transaction, simulate) ->
		simulate ? StorageUtil.simulateInsert(inventory, resource, maxAmount, transaction)
			: inventory.insert(resource, maxAmount, transaction)
	),
	OUT((inventory, resource, maxAmount, transaction, simulate) ->
		simulate ? StorageUtil.simulateExtract(inventory, resource, maxAmount, transaction)
			: inventory.extract(resource, maxAmount, transaction)
	);

	private final StorageInteraction<ItemVariant> interaction;

	MoveDirection(StorageInteraction<ItemVariant> interaction) {
		this.interaction = interaction;
	}

	public long moveResources(Storage<ItemVariant> inventory, ItemVariant resource, long maxAmount, Transaction transaction, boolean simulate) {
		return this.interaction.moveResources(inventory, resource, maxAmount, transaction, simulate);
	}
}
