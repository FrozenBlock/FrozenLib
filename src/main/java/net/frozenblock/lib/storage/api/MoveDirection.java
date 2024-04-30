/*
 * Copyright 2023 FrozenBlock
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
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
