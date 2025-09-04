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

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.math.api.AdvancedMath;
import net.frozenblock.lib.particle.client.options.WindParticleOptions;
import net.frozenblock.lib.wind.client.impl.ClientWindManager;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.QuadParticleRenderState;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class WindParticle extends SingleQuadParticle {
	private static final Vector3f NORMALIZED_QUAT_VECTOR = new Vector3f(0.5F, 0.5F, 0.5F).normalize();
	private final SpriteSet spriteSet;
	private int ageBeforeDissipating;

	private float prevYRot;
	private float yRot;
	private float prevXRot;
	private float xRot;
	private float prevRotMultiplier;
	private float rotMultiplier;
	private boolean shouldDissipate;
	private boolean flipped;
	private boolean chosenSide;

	WindParticle(@NotNull ClientLevel level, @NotNull SpriteSet spriteSet, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
		super(level, x, y, z, velocityX, velocityY, velocityZ, spriteSet.first());
		this.quadSize *= 3F;
		this.setSize(0.3F, 0.3F);
		this.spriteSet = spriteSet;
		this.lifetime = 19;
		this.hasPhysics = false;
		this.friction = 0.95F;
		this.gravity = 0F;
	}

	@Override
	public void tick() {
		super.tick();

		this.prevYRot = this.yRot;
		this.prevXRot = this.xRot;

		if (!this.shouldDissipate) {
			double multXZ = 0.007D;
			double multY = 0.0015D * 0.695;
			Vec3 pos = new Vec3(this.x, this.y, this.z);
			Vec3 wind = ClientWindManager.getWindMovement(this.level, pos, 1D, 7D, 5D);
			this.xd += wind.x() * multXZ;
			this.yd += wind.y() * multY;
			this.zd += wind.z() * multXZ;

			double horizontalDistance = Math.sqrt((this.xd * this.xd) + (this.zd * this.zd));

			double newYRot = (Mth.atan2(this.xd, this.zd)) * Mth.RAD_TO_DEG;
			double newXRot = (Mth.atan2(horizontalDistance, this.yd)) * Mth.RAD_TO_DEG;

			if (Math.abs(newYRot - this.prevYRot) > 180D) newYRot += 360D;
			if (Math.abs(newXRot - this.prevXRot) > 180D) newXRot += 360D;

			double newYRotDifference = newYRot - this.yRot;
			double newXRotDifference = newXRot - this.xRot;

			this.yRot += (float)newYRotDifference * 0.25F;
			this.xRot += (float)newXRotDifference * 0.25F;

			if (this.yRot > 360F) {
				this.yRot -= 360F;
				this.prevYRot -= 360F;
			} else if (this.yRot < 0F) {
				this.yRot += 360F;
				this.prevYRot += 360F;
			}

			if (this.xRot > 360F) {
				this.xRot -= 360F;
				this.prevXRot -= 360F;
			} else if (this.xRot < 0F) {
				this.xRot += 360F;
				this.prevXRot += 360F;
			}
		} else {
			this.friction = 0.2F;
		}

		this.prevRotMultiplier = this.rotMultiplier;
		this.rotMultiplier = (float) Math.sin((this.xRot * Mth.PI) / 180F);

        if (this.shouldDissipate && this.age > 7 && this.age < this.ageBeforeDissipating) {
			this.age = this.ageBeforeDissipating;
		}

		if (this.age >= this.ageBeforeDissipating && !this.chosenSide) {
			this.chosenSide = true;
			this.flipped = this.level.random.nextBoolean();
			this.lifetime = this.ageBeforeDissipating + 11;
		}
		this.setSpriteFromAge(this.spriteSet);
	}

	@Override
	public void move(double x, double y, double z) {
		if (!this.shouldDissipate) {
			double d = x;
			double e = y;
			double f = z;
			if ((x != 0D || y != 0D || z != 0D) && x * x + y * y + z * z < Mth.square(100D)) {
				Vec3 vec3 = Entity.collideBoundingBox(null, new Vec3(x, y, z), this.getBoundingBox(), this.level, List.of());
				x = vec3.x;
				y = vec3.y;
				z = vec3.z;
			}
			Vec3 vec3 = new Vec3(d, e, f);
			boolean canDissipate = this.age > 7;

			if (canDissipate && vec3.length() < 0.0065D) {
				this.shouldDissipate = true;
			}
			if (x != 0D || y != 0D || z != 0D) {
				this.setBoundingBox(this.getBoundingBox().move(x, y, z));
				this.setLocationFromBoundingbox();
			} else if (canDissipate) {
				this.shouldDissipate = true;
			}

			if (Math.abs(e) >= 1.0E-5F && Math.abs(y) < 1.0E-5F) {
				this.shouldDissipate = true;
			}

			this.onGround = e != y && e < 0D;
			if (d != x) {
				this.xd = 0D;
				this.shouldDissipate = true;
			}

			if (f != z) {
				this.zd = 0D;
				this.shouldDissipate = true;
			}
		}
	}

	@Override
	public void setSpriteFromAge(@NotNull SpriteSet spriteSet) {
		if (this.removed) return;
		int i = this.age < 8 ? this.age : (this.age < this.ageBeforeDissipating ? 8 : this.age - (this.ageBeforeDissipating) + 9);
		this.setSprite(spriteSet.get(Math.min(i, 20), 20));
	}

	@Override
	public void extract(QuadParticleRenderState renderState, @NotNull Camera camera, float partialTicks) {
		final float yRot = Mth.lerp(partialTicks, this.prevYRot, this.yRot) * Mth.DEG_TO_RAD;
		final float xRot = Mth.lerp(partialTicks, this.prevXRot, this.xRot) * -Mth.DEG_TO_RAD;
		final float cameraRotWhileVertical = ((-camera.getYRot()) * (1F - Mth.lerp(partialTicks, this.prevRotMultiplier, this.rotMultiplier))) * Mth.DEG_TO_RAD;
		float cameraXRot = camera.getXRot();
		final float rotationOffset = 90F;

		final float x = (float)(Mth.lerp(partialTicks, this.xo, this.x));
		final float y = (float)(Mth.lerp(partialTicks, this.yo, this.y));
		final float z = (float)(Mth.lerp(partialTicks, this.zo, this.z));
		final Vec3 particlePos = new Vec3(x, y, z);
		final Vec3 cameraPos = camera.getPosition();
		double normalizedRelativeY = particlePos.subtract(cameraPos).normalize().subtract(new Vec3(this.xd, this.yd, this.zd).normalize()).normalize().y;

		final double particleRotation = AdvancedMath.getAngleFromOriginXZ(new Vec3(this.xd, 0D, this.zd));
		final double angleToParticle = AdvancedMath.getAngleBetweenXZ(particlePos, cameraPos);
		final double relativeParticleAngle = (360D + (angleToParticle - particleRotation)) % 360D;

		if (normalizedRelativeY > 0D) {
			cameraXRot = Mth.lerp(Mth.square((float)normalizedRelativeY), cameraXRot, -90F);
		} else if (normalizedRelativeY < 0D) {
			cameraXRot = Mth.lerp(Mth.square((float)Math.abs(normalizedRelativeY)), cameraXRot, 90F);
		}

		float fixedRotation = rotationOffset + cameraXRot;
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
			this.getLayer(),
			(float) (x - cameraPos.x()),
			(float) (y - cameraPos.y()),
			(float) (z - cameraPos.z()),
			quatA,
			quatB,
			this.getQuadSize(partialTicks),
			!this.flipped ? this.getU0() : this.getU1(),
			!this.flipped ? this.getU1() : this.getU0(),
			this.getV0(),
			this.getV1(),
			ARGB.colorFromFloat(this.alpha, this.rCol, this.gCol, this.bCol),
			this.getLightColor(partialTicks)
		);
	}

	private static void submit(
		@NotNull QuadParticleRenderState renderState,
		Layer layer,
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

	private static Quaternionf createBaseQuaternion() {
		return new Quaternionf().setAngleAxis(0F, NORMALIZED_QUAT_VECTOR.x(), NORMALIZED_QUAT_VECTOR.y(), NORMALIZED_QUAT_VECTOR.z());
	}

	@Override
	protected @NotNull Layer getLayer() {
		return Layer.TRANSLUCENT;
	}

	@Environment(EnvType.CLIENT)
	public record Factory(@NotNull SpriteSet spriteProvider) implements ParticleProvider<WindParticleOptions> {

		@Override
		@NotNull
		public Particle createParticle(
			@NotNull WindParticleOptions options,
			@NotNull ClientLevel level,
			double x, double y, double z,
			double xSpeed, double ySpeed, double zSpeed,
			@NotNull RandomSource random
		) {
			Vec3 velocity = options.getVelocity();
			WindParticle windParticle = new WindParticle(level, this.spriteProvider, x, y, z, 0D, 0D, 0D);
			windParticle.ageBeforeDissipating = options.getLifespan();
			windParticle.lifetime += windParticle.ageBeforeDissipating;
			windParticle.flipped = random.nextBoolean();
			windParticle.xd = velocity.x;
			windParticle.zd = velocity.z;
			windParticle.yd = velocity.y;
			double horizontalDistance = Math.sqrt((windParticle.xd * windParticle.xd) + (windParticle.zd * windParticle.zd));
			windParticle.yRot += (float) (Mth.atan2(windParticle.xd, windParticle.zd)) * Mth.RAD_TO_DEG;
			windParticle.xRot += (float) (Mth.atan2(horizontalDistance, windParticle.yd)) * Mth.RAD_TO_DEG;

			windParticle.prevYRot = windParticle.yRot;
			windParticle.prevXRot = windParticle.xRot;
			return windParticle;
		}
	}
}
