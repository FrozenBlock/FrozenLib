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

package net.frozenblock.lib.particle.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.math.api.AdvancedMath;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.QuadParticleRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class CustomRotationalParticleHelper {
	private static final Vector3f NORMALIZED_QUAT_VECTOR = new Vector3f(0.5F, 0.5F, 0.5F).normalize();

	private float prevXRot;
	private float xRot;
	private float prevYRot;
	private float yRot;

	private float prevRotMultiplier;
	private float rotMultiplier;

	private boolean flipped;

	public CustomRotationalParticleHelper() {
	}

	public void setPrevRotationFromCurrent() {
		this.prevYRot = this.yRot;
		this.prevXRot = this.xRot;
	}

	public void setRotation(float yRot, float xRot) {
		this.yRot = yRot;
		this.xRot = xRot;
		this.setRotMultiplierFromXRot();
	}

	public void setRotationFromMovement(double xd, double yd, double zd, float rotationAmount) {
		final double horizontalDistance = Math.sqrt((xd * xd) + (zd * zd));
		double newYRot = (Mth.atan2(xd, zd)) * Mth.RAD_TO_DEG;
		double newXRot = (Mth.atan2(horizontalDistance, yd)) * Mth.RAD_TO_DEG;

		if (Math.abs(newYRot - this.yRot) > 180D) newYRot += 360D;
		if (Math.abs(newXRot - this.xRot) > 180D) newXRot += 360D;

		double newYRotDifference = newYRot - this.yRot;
		double newXRotDifference = newXRot - this.xRot;

		this.yRot += (float)newYRotDifference * rotationAmount;
		this.xRot += (float)newXRotDifference * rotationAmount;

		this.setRotMultiplierFromXRot();
	}

	private void setRotMultiplierFromXRot() {
		this.prevRotMultiplier = this.rotMultiplier;
		this.rotMultiplier = Mth.sin((this.xRot * Mth.PI) / 180F);
	}

	public void setFlipped(boolean flipped) {
		this.flipped = flipped;
	}

	private static float [] getUVMapping(float U0, float U1, float V0, float V1, boolean flipped) {
		return new float[]{
			flipped ? U1 : U0,
			flipped ? U0 : U1,
			V0,
			V1
		};
	}

	private static Quaternionf createBaseQuaternion() {
		return new Quaternionf().setAngleAxis(0F, NORMALIZED_QUAT_VECTOR.x(), NORMALIZED_QUAT_VECTOR.y(), NORMALIZED_QUAT_VECTOR.z());
	}

	public void extract(
		QuadParticleRenderState renderState,
		@NotNull Camera camera,
		SingleQuadParticle.Layer layer,
		double x, double y, double z,
		double xd, double yd, double zd,
		float U0, float U1, float V0, float V1,
		float quadSize,
		int color,
		int lightColor,
		float partialTicks
	) {
		final float yRot = Mth.lerp(partialTicks, this.prevYRot, this.yRot) * Mth.DEG_TO_RAD;
		final float xRot = Mth.lerp(partialTicks, this.prevXRot, this.xRot) * -Mth.DEG_TO_RAD;
		final float cameraRotWhileVertical = ((-camera.getYRot()) * (1F - Mth.lerp(partialTicks, this.prevRotMultiplier, this.rotMultiplier))) * Mth.DEG_TO_RAD;
		float cameraXRot = camera.getXRot();

		final Vec3 particlePos = new Vec3(x, y, z);
		final Vec3 cameraPos = camera.getPosition();
		double normalizedRelativeY = particlePos.subtract(cameraPos).normalize().subtract(new Vec3(xd, yd, zd).normalize()).normalize().y;

		final double particleRotation = AdvancedMath.getAngleFromOriginXZ(new Vec3(xd, 0D, zd));
		final double angleToParticle = AdvancedMath.getAngleBetweenXZ(particlePos, cameraPos);
		final double relativeParticleAngle = (360D + (angleToParticle - particleRotation)) % 360D;

		if (normalizedRelativeY > 0D) {
			cameraXRot = Mth.lerp(Mth.square((float)normalizedRelativeY), cameraXRot, -90F);
		} else if (normalizedRelativeY < 0D) {
			cameraXRot = Mth.lerp(Mth.square((float)Math.abs(normalizedRelativeY)), cameraXRot, 90F);
		}

		float fixedRotation = 90F + cameraXRot;
		if (relativeParticleAngle > 0D && relativeParticleAngle < 180D) fixedRotation *= -1F;

		final float cameraRotWhileSideways = ((fixedRotation) * (Mth.lerp(partialTicks, this.prevRotMultiplier, this.rotMultiplier))) * Mth.DEG_TO_RAD;

		final Quaternionf quatA = createBaseQuaternion()
			.rotateY(yRot)
			.rotateX(-xRot)
			.rotateY(cameraRotWhileSideways)
			.rotateY(cameraRotWhileVertical);

		final Quaternionf quatB = createBaseQuaternion()
			.rotateY(-Mth.PI + yRot)
			.rotateX(xRot)
			.rotateY(cameraRotWhileSideways)
			.rotateY(cameraRotWhileVertical);

		submit(
			renderState,
			layer,
			(float) (x - cameraPos.x()),
			(float) (y - cameraPos.y()),
			(float) (z - cameraPos.z()),
			quatA,
			quatB,
			quadSize,
			!this.flipped ? U0 : U1,
			!this.flipped ? U1 : U0,
			V0,
			V1,
			color,
			lightColor
		);
	}

	private static void submit(
		@NotNull QuadParticleRenderState renderState,
		SingleQuadParticle.Layer layer,
		float x, float y, float z,
		@NotNull Quaternionf quatA, @NotNull Quaternionf quatB,
		float quadSize,
		float uA, float uB, float vA, float vB,
		int color,
		int lightColor
	) {
		renderState.add(
			layer,
			x, y, z,
			quatA.x, quatA.y, quatA.z, quatA.w,
			quadSize,
			uA, uB, vA, vB,
			color,
			lightColor
		);
		renderState.add(
			layer,
			x, y, z,
			quatB.x, quatB.y, quatB.z, quatB.w,
			quadSize,
			uB, uA, vA, vB,
			color,
			lightColor
		);
	}

}
