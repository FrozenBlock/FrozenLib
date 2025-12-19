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

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.particle.api.client.CustomRotationalParticleHelper;
import net.frozenblock.lib.particle.options.WindParticleOptions;
import net.frozenblock.lib.tag.api.FrozenBlockTags;
import net.frozenblock.lib.wind.client.impl.ClientWindManager;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.state.QuadParticleRenderState;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Contract;

@Environment(EnvType.CLIENT)
public class WindParticle extends SingleQuadParticle {
	protected final CustomRotationalParticleHelper rotationalHelper = new CustomRotationalParticleHelper();
	private final SpriteSet spriteSet;
	private final double windMovementScale;
	private final float rotationChangeAmount;

	protected int ageBeforeDissipating;
	private boolean shouldDissipate;
	private boolean chosenSide;

	WindParticle(
		ClientLevel level,
		SpriteSet spriteSet,
		WindParticleOptions.ParticleLength particleLength,
		double x, double y, double z,
		double xd, double yd, double zd
	) {
		super(level, x, y, z, xd, yd, zd, spriteSet.first());
		this.quadSize *= 3F * particleLength.getQuadSizeScale();
		this.setSize(0.3F, 0.3F);
		this.spriteSet = spriteSet;
		this.windMovementScale = particleLength.getWindMovementScale();
		this.rotationChangeAmount = particleLength.getRotationChangeAmount();

		this.lifetime = 19;
		this.hasPhysics = false;
		this.friction = 0.95F;
		this.gravity = 0F;
		this.rotationalHelper.setFlipped(level.getRandom().nextBoolean());
	}

	@Override
	public void tick() {
		super.tick();

		this.rotationalHelper.setPrevRotationFromCurrent();

		if (!this.shouldDissipate) {
			final double horizontalScale = 0.007D;
			final double verticalScale = 0.0015D * 0.695;
			final Vec3 pos = new Vec3(this.x, this.y, this.z);
			final Vec3 wind = ClientWindManager.getWindMovement(this.level, pos, this.windMovementScale, 7D, 5D);
			this.xd += wind.x() * horizontalScale;
			this.yd += wind.y() * verticalScale;
			this.zd += wind.z() * horizontalScale;

			this.setRotationFromMovement(this.rotationChangeAmount);
		} else {
			this.friction = 0.2F;
		}

        if (this.shouldDissipate && this.age > 7 && this.age < this.ageBeforeDissipating) this.age = this.ageBeforeDissipating;

		if (this.age >= this.ageBeforeDissipating && !this.chosenSide) {
			this.chosenSide = true;
			this.rotationalHelper.setFlipped(this.level.getRandom().nextBoolean());
			this.lifetime = this.ageBeforeDissipating + 11;
		}
		this.setSpriteFromAge(this.spriteSet);
	}

	public void setRotationFromMovement(float rotationAmount) {
		this.rotationalHelper.setRotationFromMovement(this.xd, this.yd, this.zd, rotationAmount);
	}

	@Override
	public void move(double x, double y, double z) {
		if (this.shouldDissipate) return;

		final double d = x;
		final double e = y;
		final double f = z;

		if ((x != 0D || y != 0D || z != 0D) && x * x + y * y + z * z < Mth.square(100D)) {
			final Vec3 vec3 = this.collideBoundingBox(new Vec3(x, y, z));
			x = vec3.x;
			y = vec3.y;
			z = vec3.z;
		}
		final Vec3 vec3 = new Vec3(d, e, f);
		final boolean canDissipate = this.age > 7;

		if (canDissipate && vec3.length() < 0.0065D) this.shouldDissipate = true;

		if (x != 0D || y != 0D || z != 0D) {
			this.setBoundingBox(this.getBoundingBox().move(x, y, z));
			this.setLocationFromBoundingbox();
		} else if (canDissipate) {
			this.shouldDissipate = true;
		}

		if (Math.abs(e) >= 1.0E-5F && Math.abs(y) < 1.0E-5F) this.shouldDissipate = true;

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

	private Vec3 collideBoundingBox(Vec3 vec3) {
		final AABB box = this.getBoundingBox();
		final List<VoxelShape> shapes = ImmutableList.copyOf(this.getBlockCollisions(box.expandTowards(vec3)));
		return Entity.collideWithShapes(vec3, box, shapes);
	}

	@Contract(pure = true)
	private Iterable<VoxelShape> getBlockCollisions(AABB box) {
		return () -> new BlockCollisions<>(
			this.level,
			CollisionContext.empty(),
			box,
			false,
			(pos, shape) -> {
				if (this.level.getBlockState(pos).is(FrozenBlockTags.BLOWING_CAN_PASS_THROUGH)) return Shapes.empty();
				return shape;
			}
		);
	}

	@Override
	public void setSpriteFromAge(SpriteSet spriteSet) {
		if (this.removed) return;
		final int frame = this.age < 8 ? this.age : (this.age < this.ageBeforeDissipating ? 8 : this.age - (this.ageBeforeDissipating) + 9);
		this.setSprite(spriteSet.get(Math.min(frame, 20), 20));
	}

	@Override
	public void extract(QuadParticleRenderState renderState, Camera camera, float partialTicks) {
		this.rotationalHelper.extract(
			renderState,
			camera,
			this.getLayer(),
			Mth.lerp(partialTicks, this.xo, this.x),
			Mth.lerp(partialTicks, this.yo, this.y),
			Mth.lerp(partialTicks, this.zo, this.z),
			this.xd,
			this.yd,
			this.zd,
			this.getU0(),
			this.getU1(),
			this.getV0(),
			this.getV1(),
			this.getQuadSize(partialTicks),
			ARGB.colorFromFloat(this.alpha, this.rCol, this.gCol, this.bCol),
			this.getLightCoords(partialTicks),
			partialTicks
		);
	}

	@Override
	protected Layer getLayer() {
		return Layer.TRANSLUCENT;
	}

	public record Factory(SpriteSet spriteSet) implements ParticleProvider<WindParticleOptions> {
		@Override
		public Particle createParticle(
			WindParticleOptions options,
			ClientLevel level,
			double x, double y, double z,
			double xSpeed, double ySpeed, double zSpeed,
			RandomSource random
		) {
			final Vec3 velocity = options.velocity();
			final WindParticle windParticle = new WindParticle(level, this.spriteSet, options.length(), x, y, z, 0D, 0D, 0D);
			windParticle.ageBeforeDissipating = options.lifespan();
			windParticle.lifetime += windParticle.ageBeforeDissipating;

			windParticle.xd = velocity.x;
			windParticle.zd = velocity.z;
			windParticle.yd = velocity.y;
			windParticle.setRotationFromMovement(1F);
			windParticle.rotationalHelper.setPrevRotationFromCurrent();

			return windParticle;
		}
	}
}
