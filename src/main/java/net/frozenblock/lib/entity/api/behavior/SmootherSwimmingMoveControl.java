/*
 * Copyright 2023 FrozenBlock
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
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
