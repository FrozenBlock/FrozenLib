/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.worldgen.feature.mixin;

import net.frozenblock.lib.worldgen.feature.impl.saved.FeatureManager;
import net.frozenblock.lib.worldgen.feature.impl.saved.ServerLevelFeatureManagerInterface;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class ServerLevelMixin implements ServerLevelFeatureManagerInterface {

	@Unique
	private FeatureManager featureManager;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void frozenLib$setFeatureManager(CallbackInfo info) {
		this.featureManager = new FeatureManager(ServerLevel.class.cast(this));
	}

	@Unique
	@Override
	public FeatureManager frozenLib$featureManager() {
		return this.featureManager;
	}
}
