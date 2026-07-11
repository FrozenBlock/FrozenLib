/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.block.mixin.fire;

import net.frozenblock.lib.block.impl.fire.FireData;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.ConversionType;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConversionType.class)
public class ConversionTypeMixin {

	@Inject(method = "convertCommon", at = @At("HEAD"))
	private static void frozenLib$setFireTypeOnConversion(Mob from, Mob to, ConversionParams params, CallbackInfo info) {
		final FireData fireData = FireData.ATTACHMENT.get(from);
		if (fireData == null) return;
		FireData.ATTACHMENT.set(to, new FireData(fireData.type()));
	}
}
