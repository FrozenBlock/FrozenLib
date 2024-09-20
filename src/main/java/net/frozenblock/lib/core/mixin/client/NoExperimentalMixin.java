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

package net.frozenblock.lib.core.mixin.client;

import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldOpenFlows.class)
public class NoExperimentalMixin {

	@Inject(
		method = "askForBackup",
		at = @At("HEAD"),
		cancellable = true,
		require = 0
	)
	private void frozenLib$preventBackupScreenAndProceed(Screen lastScreen, String levelName, boolean customized, Runnable loadLevel, CallbackInfo ci) {
		if (FrozenLibConfig.get().removeExperimentalWarning)  {
			ci.cancel();
			loadLevel.run();
		}
	}

	@ModifyVariable(method = "confirmWorldCreation", at = @At("HEAD"), argsOnly = true, ordinal = 0, require = 0)
	private static boolean frozenLib$skipCreationWarning(boolean original) {
		if (FrozenLibConfig.get().removeExperimentalWarning)  {
			return true;
		}
		return original;
	}

}
