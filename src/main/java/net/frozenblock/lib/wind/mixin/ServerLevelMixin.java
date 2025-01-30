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

package net.frozenblock.lib.wind.mixin;

import net.frozenblock.lib.wind.api.WindManager;
import net.frozenblock.lib.wind.impl.WindManagerInterface;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin implements WindManagerInterface {

	@Shadow
	public abstract DimensionDataStorage getDataStorage();

	@Unique
	private WindManager frozenLib$windManager;

	@Unique
	@Override
	public WindManager frozenLib$getOrCreateWindManager() {
		if (this.frozenLib$windManager == null) {
			this.frozenLib$windManager = this.getDataStorage().computeIfAbsent(WindManager.TYPE);
		}

		return this.frozenLib$windManager;
	}

}
