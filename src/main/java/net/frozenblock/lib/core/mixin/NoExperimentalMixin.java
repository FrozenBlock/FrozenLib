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

package net.frozenblock.lib.core.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.serialization.Lifecycle;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WorldOpenFlows.class)
public class NoExperimentalMixin {

    @ModifyExpressionValue(
		method = "loadLevel",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/WorldOptions;isOldCustomizedWorld()Z")
	)
    private boolean frozenLib$markAsNotCustomized(boolean original) {
		if (FrozenLibConfig.get().removeExperimentalWarning) return false;
		return original;
	}

	@ModifyExpressionValue(
		method = "loadLevel",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/WorldData;worldGenSettingsLifecycle()Lcom/mojang/serialization/Lifecycle;")
	)
	private Lifecycle frozenLib$markAsStable(Lifecycle original) {
		if (FrozenLibConfig.get().removeExperimentalWarning) return Lifecycle.stable();
		return original;
	}

}
