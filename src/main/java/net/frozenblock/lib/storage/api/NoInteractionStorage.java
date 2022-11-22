/*
 * Copyright 2022 FrozenBlock
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

import java.util.Iterator;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public interface NoInteractionStorage<T> extends Storage<T> {

	@Override
	default long insert(T resource, long maxAmount, TransactionContext transaction) {
		return 0;
	}

	@Override
	default long extract(T resource, long maxAmount, TransactionContext transaction) {
		return 0;
	}

	@Override
	default boolean supportsInsertion() {
		return false;
	}

	@Override
	default boolean supportsExtraction() {
		return false;
	}

	@Override
	default Iterator<StorageView<T>> iterator() {
		return new Iterator<>() {
			@Override
			public boolean hasNext() {
				return false;
			}

			@Override
			public StorageView<T> next() {
				return null;
			}
		};
	}
}
