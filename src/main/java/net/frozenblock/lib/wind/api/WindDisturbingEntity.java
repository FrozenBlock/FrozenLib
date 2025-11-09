/*
 * Copyright (C) 2024-2025 FrozenBlock
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

package net.frozenblock.lib.wind.api;

import net.minecraft.resources.Identifier;

/**
 * Used to append a wind disturbance to an entity, such as the Breeze.
 */
public interface WindDisturbingEntity {

	/**
	 * @return the {@link Identifier} of the registered {@link WindDisturbanceLogic} to use for this entity.
	 */
	Identifier frozenLib$getWindDisturbanceLogicID();

	/**
	 * @return the width of the wind disturbance.
	 */
	double frozenLib$getWindWidth();

	/**
	 * @return the height of the wind disturbance.
	 */
	double frozenLib$getWindHeight();

	/**
	 * @return the vertical offset of the wind disturbance from the entity's center.
	 */
	double frozenLib$getWindAreaYOffset();

	/**
	 * @return whether this entity will sync its wind disturbance to the client via packets, or will create the disturbance separately on the server and client.
	 */
	boolean frozenLib$useSyncPacket();

}
