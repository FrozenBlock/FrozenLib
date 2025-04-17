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

package net.frozenblock.lib.modmenu.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.terraformersmc.modmenu.gui.widget.ModListWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.cape.client.api.ClientCapeUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Set;

@Pseudo
@Environment(EnvType.CLIENT)
@Mixin(ModListWidget.class)
public class ModListWidgetMixin {

	@WrapOperation(
		method = "filter(Ljava/lang/String;ZZ)V",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z",
			ordinal = 0
		),
		require = 0,
		remap = false
	)
	private boolean frozenLib$showFrozenLibIfPlayerHasCapes(
		Set instance, Object object, Operation<Boolean> original,
		@Local(ordinal = 1) String modId
	) {
		if (modId.equals(FrozenLibConstants.MOD_ID) && ClientCapeUtil.hasUsableCapes(true)) {
			return false;
		}
		return original.call(instance, object);
	}
}
