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

package net.frozenblock.lib.config.api.sync;

import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;

/**
 * Used to provide the behavior for a config entry when syncing is in action.
 * See {@link FrozenLibConfig} for an example.
 * @since 1.5.2
 */
public enum SyncBehavior {

	/**
	 * The default behavior for all entries.
	 * Operators of the server and LAN hosts will be able to set the config values, and all others will see that value update on their client.
	 * Non-operator/host clients will not be able to modify these values, as they will be locked in the GUI (currently we provide native support for Cloth Config only,) and display the reason why it can't be modified.
	 * The values synced to the client will not save to their config, as this is simply a temporary modification.
	 */
	SYNCABLE(true),

	/**
	 * The config entry will not sync, and will be modifiable to anyone's client.
	 * This should be used for client-only options, for instance, whether or not you want a specific particle to spawn.
	 */
	UNSYNCABLE(false),

	/**
	 * The config entry will be locked whilst connected to the server or LAN world, but will respect the client's value as it will not sync.
	 * Anyone will be able to leave, change the value, then rejoin and have their own option enabled.
	 * This is recommended to be used for client-only options that may cause bugs or other issues if changed during gameplay.
	 * Keep in mind that operators/hosts will be able to modify this entry at any time for themselves.
	 */
	LOCK_WHEN_SYNCED(false);

	private final boolean canSync;

	SyncBehavior(boolean canSync) {
		this.canSync = canSync;
	}

	public boolean canSync() {
		return this.canSync;
	}
}
