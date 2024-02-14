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
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.minecraft.client.gui.screens.BackupConfirmScreen;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(WorldOpenFlows.class)
public class NoExperimentalMixin {

    @ModifyExpressionValue(
		method = "askForBackup",
		at = @At(
			value = "NEW",
			target = "(Ljava/lang/Runnable;Lnet/minecraft/client/gui/screens/BackupConfirmScreen$Listener;Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/Component;Z)Lnet/minecraft/client/gui/screens/BackupConfirmScreen;"
		)
	)
    private BackupConfirmScreen frozenLib$proceed(BackupConfirmScreen original) {
		if (FrozenLibConfig.get().removeExperimentalWarning)  {
			original.onProceed.proceed(false, false);
		}
		return original;
	}

	@ModifyVariable(method = "confirmWorldCreation", at = @At("HEAD"), argsOnly = true, ordinal = 0)
	private static boolean frozenLib$skipCreationWarning(boolean original) {
		if (FrozenLibConfig.get().removeExperimentalWarning)  {
			return true;
		}
		return original;
	}

}
