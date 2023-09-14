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

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

/**
 * @since 1.3.8
 */
public enum MoveDirection {
	IN(Storage::insert),
	OUT(Storage::extract);

	private final StorageInteraction<Object> interaction;

	MoveDirection(StorageInteraction<Object> interaction) {
		this.interaction = interaction;
	}

	@SuppressWarnings("unchecked")
	public <T> long moveResources(Storage<T> inventory, T resource, long maxAmount, TransactionContext transaction) {
		return this.interaction.moveResources((Storage<Object>) inventory, resource, maxAmount, transaction);
	}

	@SuppressWarnings("unchecked")
	public <T> long simulateMoveResources(Storage<T> inventory, T resource, long maxAmount, TransactionContext transaction) {
		return this.interaction.moveResources((Storage<Object>) inventory, resource, maxAmount, transaction, true);
	}
}
