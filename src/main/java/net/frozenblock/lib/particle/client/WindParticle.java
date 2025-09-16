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
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.tag.api.FrozenBlockTags;
import net.frozenblock.lib.wind.client.impl.ClientWindManager;
import net.frozenblock.lib.particle.client.options.WindParticleOptions;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import java.util.List;

@Environment(EnvType.CLIENT)
public class WindParticle extends TextureSheetParticle {
	private final CustomRotationalParticleRenderer rotationalRenderer = new CustomRotationalParticleRenderer();
	private final SpriteSet spriteProvider;
	private int ageBeforeDissipating;

	private boolean shouldDissipate;
	private boolean chosenSide;

	WindParticle(@NotNull ClientLevel level, @NotNull SpriteSet spriteProvider, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
		super(level, x, y, z, velocityX, velocityY, velocityZ);
		this.quadSize *= 3F;
		this.setSize(0.3F, 0.3F);
		this.spriteProvider = spriteProvider;
		this.setSpriteFromAge(spriteProvider);
		this.lifetime = 19;
		this.hasPhysics = false;
		this.friction = 0.95F;
		this.gravity = 0F;
		this.rotationalRenderer.setFlipped(level.random.nextBoolean());
	}

	@Override
	public void tick() {
		super.tick();

		this.rotationalRenderer.setPrevRotationFromCurrent();

		if (!this.shouldDissipate) {
			double multXZ = 0.007D;
			double multY = 0.0015D * 0.695;
			Vec3 pos = new Vec3(this.x, this.y, this.z);
			Vec3 wind = ClientWindManager.getWindMovement(this.level, pos, 1D, 7D, 5D);
			this.xd += wind.x() * multXZ;
			this.yd += wind.y() * multY;
			this.zd += wind.z() * multXZ;

			this.setRotationFromMovement(0.25F);
		} else {
			this.friction = 0.2F;
		}

        if (this.shouldDissipate && this.age > 7 && this.age < this.ageBeforeDissipating) {
			this.age = this.ageBeforeDissipating;
		}

		if (this.age >= this.ageBeforeDissipating && !this.chosenSide) {
			this.chosenSide = true;
			this.rotationalRenderer.setFlipped(this.level.random.nextBoolean());
			this.lifetime = this.ageBeforeDissipating + 11;
		}
		this.setSpriteFromAge(this.spriteProvider);
	}

	public void setRotationFromMovement(float rotationAmount) {
		this.rotationalRenderer.setRotationFromMovement(this.xd, this.yd, this.zd, rotationAmount);
	}

	@Override
	public void move(double x, double y, double z) {
		if (this.shouldDissipate) return;

		final double d = x;
		final double e = y;
		final double f = z;

		if ((x != 0D || y != 0D || z != 0D) && x * x + y * y + z * z < Mth.square(100D)) {
			Vec3 vec3 = this.collideBoundingBox(new Vec3(x, y, z));
			x = vec3.x;
			y = vec3.y;
			z = vec3.z;
		}
		Vec3 vec3 = new Vec3(d, e, f);
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

	private @NotNull Vec3 collideBoundingBox(Vec3 vec3) {
		final AABB box = this.getBoundingBox();
		final List<VoxelShape> shapes = ImmutableList.copyOf(this.getBlockCollisions(box.expandTowards(vec3)));
		return Entity.collideWithShapes(vec3, box, shapes);
	}

	@Contract(pure = true)
	private @NotNull Iterable<VoxelShape> getBlockCollisions(AABB box) {
		return () -> new BlockCollisions<>(
			this.level,
			null,
			box,
			false,
			(pos, shape) -> {
				if (this.level.getBlockState(pos).is(FrozenBlockTags.BLOWING_CAN_PASS_THROUGH)) return Shapes.empty();
				return shape;
			}
		);
	}

	@Override
	public void setSpriteFromAge(@NotNull SpriteSet spriteProvider) {
		if (this.removed) return;
		int i = this.age < 8 ? this.age : (this.age < this.ageBeforeDissipating ? 8 : this.age - (this.ageBeforeDissipating) + 9);
		this.setSprite(spriteProvider.get(Math.min(i, 20), 20));
	}

	@Override
	public void render(@NotNull VertexConsumer buffer, @NotNull Camera camera, float partialTicks) {
		this.rotationalRenderer.render(
			buffer,
			camera,
			(float) Mth.lerp(partialTicks, this.xo, this.x),
			(float) Mth.lerp(partialTicks, this.yo, this.y),
			(float) Mth.lerp(partialTicks, this.zo, this.z),
			this.xd, this.yd, this.zd,
			this.getU0(), this.getU1(), this.getV0(), this.getV1(),
			this.getQuadSize(partialTicks),
			this.rCol, this.gCol, this.bCol, this.alpha,
			this.getLightColor(partialTicks),
			partialTicks
		);
	}

	@Override
	@NotNull
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Environment(EnvType.CLIENT)
	public record Factory(@NotNull SpriteSet spriteProvider) implements ParticleProvider<WindParticleOptions> {
		@Override
		@NotNull
		public Particle createParticle(@NotNull WindParticleOptions options, @NotNull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			Vec3 velocity = options.getVelocity();
			WindParticle windParticle = new WindParticle(level, this.spriteProvider, x, y, z, 0D, 0D, 0D);
			windParticle.ageBeforeDissipating = options.getLifespan();
			windParticle.lifetime += windParticle.ageBeforeDissipating;

			windParticle.xd = velocity.x;
			windParticle.zd = velocity.z;
			windParticle.yd = velocity.y;
			windParticle.setRotationFromMovement(1F);
			windParticle.rotationalRenderer.setPrevRotationFromCurrent();

			return windParticle;
		}
	}

}
