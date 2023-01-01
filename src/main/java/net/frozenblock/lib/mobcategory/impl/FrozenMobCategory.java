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

package net.frozenblock.lib.mobcategory.impl;

public class FrozenMobCategory {
	public final String modId;
	public final String name;
	public final int max;
	public final boolean isFriendly;
	public final boolean isPersistent;
	public final int despawnDistance;

	public FrozenMobCategory(String modId, String name, int max, boolean isFriendly, boolean isPersistent, int despawnDistance) {
		this.modId = modId;
		this.name = name;
		this.max = max;
		this.isFriendly = isFriendly;
		this.isPersistent = isPersistent;
		this.despawnDistance = despawnDistance;
	}
}
