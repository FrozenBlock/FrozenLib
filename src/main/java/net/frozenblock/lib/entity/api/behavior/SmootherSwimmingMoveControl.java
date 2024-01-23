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

package net.frozenblock.lib.entity.api.behavior;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;

public class SmootherSwimmingMoveControl extends MoveControl {
	private final float maxTurnX;
	private final float maxTurnY;
	private final float inWaterSpeedModifier;
	private final float outsideWaterSpeedModifier;
	private final boolean applyGravity;

	public SmootherSwimmingMoveControl(Mob mob, float maxTurnX, float maxTurnY, float inWaterSpeedModifier, float outsideWaterSpeedModifier, boolean applyGravity) {
		super(mob);
		this.maxTurnX = maxTurnX;
		this.maxTurnY = maxTurnY;
		this.inWaterSpeedModifier = inWaterSpeedModifier;
		this.outsideWaterSpeedModifier = outsideWaterSpeedModifier;
		this.applyGravity = applyGravity;
	}

	@Override
	public void tick() {
		if (this.applyGravity && this.mob.isInWater()) {
			this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(0.0, 0.005, 0.0));
		}

		if (this.operation == MoveControl.Operation.MOVE_TO && !this.mob.getNavigation().isDone()) {
			double d = this.wantedX - this.mob.getX();
			double e = this.wantedY - this.mob.getY();
			double f = this.wantedZ - this.mob.getZ();
			double g = d * d + e * e + f * f;
			if (g < 2.5000003E-7F) {
				this.mob.setZza(0.0F);
			} else {
				float h = (float)(Mth.atan2(f, d) * 180.0F / (float)Math.PI) - 90.0F;
				this.mob.setYRot(this.rotlerp(this.mob.getYRot(), h, (float)this.maxTurnY));
				this.mob.yBodyRot = this.mob.getYRot();
				this.mob.yHeadRot = this.mob.getYRot();
				float i = (float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
				if (this.mob.isInWater()) {
					this.mob.setSpeed(i * this.inWaterSpeedModifier);
					double j = Math.sqrt(d * d + f * f);
					if (Math.abs(e) > 1.0E-5F || Math.abs(j) > 1.0E-5F) {
						float k = -((float)(Mth.atan2(e, j) * 180.0F / (float)Math.PI));
						k = Mth.clamp(Mth.wrapDegrees(k), (float)(-this.maxTurnX), (float)this.maxTurnX);
						this.mob.setXRot(this.rotlerp(this.mob.getXRot(), k, 5.0F));
					}

					float k = Mth.cos(this.mob.getXRot() * (float) (Math.PI / 180.0));
					float l = Mth.sin(this.mob.getXRot() * (float) (Math.PI / 180.0));
					this.mob.zza = k * i;
					this.mob.yya = -l * i;
				} else {
					this.mob.setSpeed(i * this.outsideWaterSpeedModifier);
				}

			}
		} else {
			this.mob.setSpeed(0.0F);
			this.mob.setXxa(0.0F);
			this.mob.setYya(0.0F);
			this.mob.setZza(0.0F);
		}
	}
}
