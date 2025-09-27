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

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.math.api.AdvancedMath;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class CustomRotationalParticleRenderer {
	private static final Vector3f NORMALIZED_QUAT_VECTOR = new Vector3f(0.5F, 0.5F, 0.5F).normalize();

	private float prevXRot;
	private float xRot;
	private float prevYRot;
	private float yRot;

	private float prevRotMultiplier;
	private float rotMultiplier;

	private boolean flipped;

	public CustomRotationalParticleRenderer() {
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
		double newYRot = Mth.atan2(xd, zd) * Mth.RAD_TO_DEG;
		double newXRot = Mth.atan2(horizontalDistance, yd) * Mth.RAD_TO_DEG;

		double newYRotDifference = Mth.wrapDegrees(newYRot - this.yRot);
		double newXRotDifference = Mth.wrapDegrees(newXRot - this.xRot);

		this.yRot += (float)newYRotDifference * rotationAmount;
		this.xRot += (float)newXRotDifference * rotationAmount;

		while (this.yRot - this.prevYRot < -180F) this.prevYRot -= 360F;
		while (this.yRot - this.prevYRot >= 180F) this.prevYRot += 360F;
		while (this.xRot - this.prevXRot < -180F) this.prevXRot -= 360F;
		while (this.xRot - this.prevXRot >= 180F) this.prevXRot += 360F;

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

	public void render(
		@NotNull VertexConsumer buffer,
		@NotNull Camera camera,
		double x, double y, double z,
		double xd, double yd, double zd,
		float U0, float U1, float V0, float V1,
		float quadSize,
		float rCol, float gCol, float bCol, float alpha,
		int light,
		float partialTicks
	) {
		final float yRot = Mth.lerp(partialTicks, this.prevYRot, this.yRot) * Mth.DEG_TO_RAD;
		final float xRot = Mth.lerp(partialTicks, this.prevXRot, this.xRot) * -Mth.DEG_TO_RAD;
		final float cameraRotWhileVertical = ((-camera.getYRot()) * (1F - Mth.lerp(partialTicks, this.prevRotMultiplier, this.rotMultiplier))) * Mth.DEG_TO_RAD;
		float cameraXRot = camera.getXRot();

		final Vec3 particlePos = new Vec3(x, y, z);
		Vec3 cameraPos = camera.getPosition();
		double normalizedRelativeY = particlePos.subtract(cameraPos).normalize().subtract(new Vec3(xd, yd, zd).normalize()).normalize().y;

		double particleRotation = AdvancedMath.getAngleFromOriginXZ(new Vec3(xd, 0D, zd));
		double angleToParticle = AdvancedMath.getAngleBetweenXZ(particlePos, cameraPos);
		double relativeParticleAngle = (360D + (angleToParticle - particleRotation)) % 360D;

		if (normalizedRelativeY > 0D) {
			cameraXRot = Mth.lerp(Mth.square((float)normalizedRelativeY), cameraXRot, -90F);
		} else if (normalizedRelativeY < 0D) {
			cameraXRot = Mth.lerp(Mth.square((float)Math.abs(normalizedRelativeY)), cameraXRot, 90F);
		}

		float fixedRotation = 90F + cameraXRot;
		if (relativeParticleAngle > 0D && relativeParticleAngle < 180D) fixedRotation *= -1F;

		float cameraRotWhileSideways = ((fixedRotation) * (Mth.lerp(partialTicks, this.prevRotMultiplier, this.rotMultiplier))) * Mth.DEG_TO_RAD;
		this.renderParticle(
			buffer,
			cameraPos,
			particlePos,
			quadSize,
			rCol, gCol, bCol, alpha,
			light,
			transforms -> transforms.rotateY(yRot)
				.rotateX(-xRot)
				.rotateY(cameraRotWhileSideways)
				.rotateY(cameraRotWhileVertical),
			getUVMapping(U0, U1, V0, V1, this.flipped)
		);
		this.renderParticle(
			buffer,
			cameraPos,
			particlePos,
			quadSize,
			rCol, gCol, bCol, alpha,
			light,
			transforms -> transforms.rotateY(-Mth.PI + yRot)
				.rotateX(xRot)
				.rotateY(cameraRotWhileSideways)
				.rotateY(cameraRotWhileVertical),
			getUVMapping(U0, U1, V0, V1, !this.flipped)
		);
	}

	private void renderParticle(
		VertexConsumer buffer,
		@NotNull Vec3 cameraPos,
		Vec3 particlePos,
		float quadSize,
		float rCol, float gCol, float bCol, float alpha,
		int light,
		@NotNull Consumer<Quaternionf> quaternionConsumer,
		float[] uvMapping
	) {
		float xDifference = (float)(particlePos.x() - cameraPos.x());
		float yDifference = (float)(particlePos.y() - cameraPos.y());
		float zDifference = (float)(particlePos.z() - cameraPos.z());
		Quaternionf quaternionf = new Quaternionf().setAngleAxis(0F, NORMALIZED_QUAT_VECTOR.x(), NORMALIZED_QUAT_VECTOR.y(), NORMALIZED_QUAT_VECTOR.z());
		quaternionConsumer.accept(quaternionf);
		Vector3f[] vector3fs = new Vector3f[]{
			new Vector3f(-1F, -1F, 0F), new Vector3f(-1F, 1F, 0F), new Vector3f(1F, 1F, 0F), new Vector3f(1F, -1F, 0F)
		};

		for(int j = 0; j < 4; ++j) {
			Vector3f vector3f2 = vector3fs[j];
			vector3f2.rotate(quaternionf);
			vector3f2.mul(quadSize);
			vector3f2.add(xDifference, yDifference, zDifference);
		}

		buffer.addVertex(vector3fs[0].x(), vector3fs[0].y(), vector3fs[0].z())
			.setUv(uvMapping[1], uvMapping[3])
			.setColor(rCol, gCol, bCol, alpha)
			.setLight(light);
		buffer.addVertex(vector3fs[1].x(), vector3fs[1].y(), vector3fs[1].z())
			.setUv(uvMapping[1], uvMapping[2])
			.setColor(rCol, gCol, bCol, alpha)
			.setLight(light);
		buffer.addVertex(vector3fs[2].x(), vector3fs[2].y(), vector3fs[2].z())
			.setUv(uvMapping[0], uvMapping[2])
			.setColor(rCol, gCol, bCol, alpha)
			.setLight(light);
		buffer.addVertex(vector3fs[3].x(), vector3fs[3].y(), vector3fs[3].z())
			.setUv(uvMapping[0], uvMapping[3])
			.setColor(rCol, gCol, bCol, alpha)
			.setLight(light);
	}

}
