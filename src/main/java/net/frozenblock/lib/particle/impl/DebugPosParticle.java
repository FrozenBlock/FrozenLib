package net.frozenblock.lib.particle.impl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class DebugPosParticle extends TextureSheetParticle {

	DebugPosParticle(@NotNull ClientLevel level, double x, double y, double z) {
		super(level, x, y, z);
		this.setSize(0.1F, 0.1F);
		this.quadSize = 1F;
		this.lifetime = 1;
		this.hasPhysics = false;
		this.gravity = 0.0F;
	}

	@Override
	public void tick() {

	}

	@Override
	@NotNull
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	@Environment(EnvType.CLIENT)
	public static class Provider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet sprite;

		public Provider(SpriteSet sprites) {
			this.sprite = sprites;
		}

		@Override
		@NotNull
		public Particle createParticle(@NotNull SimpleParticleType defaultParticleType, @NotNull ClientLevel clientLevel, double x, double y, double z, double g, double h, double i) {
			DebugPosParticle debugPosParticle = new DebugPosParticle(clientLevel, x, y, z);
			debugPosParticle.pickSprite(this.sprite);
			return debugPosParticle;
		}
	}
}
