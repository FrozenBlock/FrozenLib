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

package net.frozenblock.lib.entity.mixin;

import net.frozenblock.lib.entity.api.NoFlopAbstractFish;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFish.class)
public abstract class AbstractFishMixin extends WaterAnimal {

	private AbstractFishMixin(EntityType<? extends WaterAnimal> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(method = "aiStep", at = @At("HEAD"), cancellable = true)
	private void frozenLib$noFlop(CallbackInfo info) {
		final AbstractFish fish = AbstractFish.class.cast(this);
		if (!(fish instanceof NoFlopAbstractFish)) return;

		super.aiStep();
		info.cancel();
	}
}
