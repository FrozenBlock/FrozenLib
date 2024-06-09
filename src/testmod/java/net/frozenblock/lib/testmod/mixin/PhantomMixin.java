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

package net.frozenblock.lib.testmod.mixin;

import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.spotting_icons.impl.EntitySpottingIconInterface;
import net.frozenblock.lib.testmod.FrozenTestMain;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Phantom.class)
public class PhantomMixin {

	@Inject(method = "<init>", at = @At("TAIL"))
	private void initWithIcon(EntityType<? extends Phantom> entityType, Level level, CallbackInfo ci) {
		Phantom phantom = Phantom.class.cast(this);
		((EntitySpottingIconInterface) phantom).getSpottingIconManager().setIcon(FrozenTestMain.id("textures/spotting_icons/phantom.png"), 16, 20, FrozenSharedConstants.id("default"));
	}
}
