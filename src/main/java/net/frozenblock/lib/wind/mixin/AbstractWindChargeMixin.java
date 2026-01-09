/*
 * Copyright (C) 2024-2026 FrozenBlock
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

import net.frozenblock.lib.wind.api.WindDisturbanceLogic;
import net.frozenblock.lib.wind.api.WindDisturbingEntity;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.projectile.hurtingprojectile.windcharge.AbstractWindCharge;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AbstractWindCharge.class)
public abstract class AbstractWindChargeMixin implements WindDisturbingEntity {

	@Unique
	@Nullable
	@Override
	public Identifier frozenLib$getWindDisturbanceLogicID() {
		return WindDisturbanceLogic.WIND_CHARGE;
	}

	@Unique
	@Override
	public double frozenLib$getWindWidth() {
		return 10D;
	}

	@Unique
	@Override
	public double frozenLib$getWindHeight() {
		return 10D;
	}

}
