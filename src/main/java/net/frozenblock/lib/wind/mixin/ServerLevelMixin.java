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

package net.frozenblock.lib.wind.mixin;

import net.frozenblock.lib.wind.api.WindManager;
import net.frozenblock.lib.wind.api.wind3d.WindManager3D;
import net.frozenblock.lib.wind.impl.WindManagerInterface;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerLevel.class)
public class ServerLevelMixin implements WindManagerInterface {

	@Unique
	private final WindManager frozenLib$windManager = new WindManager(ServerLevel.class.cast(this));

	@Unique
	private final WindManager3D frozenLib$windManager3D = new WindManager3D(ServerLevel.class.cast(this));

	@Unique
	@Override
	public WindManager frozenLib$getWindManager() {
		return this.frozenLib$windManager;
	}

	@Unique
	@Override
	public WindManager3D frozenLib$getWindManager3D() {
		return this.frozenLib$windManager3D;
	}

}
