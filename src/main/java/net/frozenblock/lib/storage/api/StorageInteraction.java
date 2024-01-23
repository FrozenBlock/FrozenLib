/*
 * Copyright 2023-2024 FrozenBlock
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
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

/**
 * @param <T> The type of resource being moved.
 * @since 1.3.8
 */
@FunctionalInterface
public interface StorageInteraction<T> {
	long moveResources(Storage<T> storage, T resource, long maxAmount, TransactionContext transaction);

	default long moveResources(Storage<T> storage, T resource, long maxAmount, TransactionContext transaction, boolean simulate) {
		if (simulate) {
			// closes this automatically
			// check functionality of AutoCloseable class
			try (Transaction simulateTransaction = Transaction.openNested(transaction)) {
				return moveResources(storage, resource, maxAmount, simulateTransaction);
			}
		}

		return moveResources(storage, resource, maxAmount, transaction);
    }
}
