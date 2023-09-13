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

package net.frozenblock.lib.storage.api;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

/**
 * @since 1.3.8
 */
public enum MoveDirection {
	IN(Storage::insert),
	OUT(Storage::extract);

	private final StorageInteraction<ItemVariant> interaction;

	MoveDirection(StorageInteraction<ItemVariant> interaction) {
		this.interaction = interaction;
	}

	public long moveResources(Storage<ItemVariant> inventory, ItemVariant resource, long maxAmount, Transaction transaction) {
		return this.interaction.moveResources(inventory, resource, maxAmount, transaction);
	}

	public long simulateMoveResources(Storage<ItemVariant> inventory, ItemVariant resource, long maxAmount, Transaction transaction) {
		return this.interaction.moveResources(inventory, resource, maxAmount, transaction, true);
	}
}
