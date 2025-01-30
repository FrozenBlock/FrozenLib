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

package net.frozenblock.lib.screenshake.mixin;

import net.frozenblock.lib.screenshake.api.ScreenShakeManager;
import net.frozenblock.lib.screenshake.impl.ScreenShakeManagerInterface;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin implements ScreenShakeManagerInterface {

	@Shadow
	public abstract DimensionDataStorage getDataStorage();

	@Unique
	private ScreenShakeManager frozenLib$screenShakeManager;

	@Unique
	@Override
	public ScreenShakeManager frozenLib$getOrCreateScreenShakeManager() {
		if (this.frozenLib$screenShakeManager == null) {
			this.frozenLib$screenShakeManager = this.getDataStorage().computeIfAbsent(ScreenShakeManager.TYPE);
		}
		return this.frozenLib$screenShakeManager;
	}

}
