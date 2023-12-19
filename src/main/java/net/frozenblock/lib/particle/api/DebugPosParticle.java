package net.frozenblock.lib.particle.api;

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

	DebugPosParticle(@NotNull ClientLevel level, @NotNull SpriteSet spriteProvider, double x, double y, double z) {
		super(level, x, y, z, 0, 0, 0);
		this.setSize(0.1F, 0.1F);
		this.pickSprite(spriteProvider);
		this.quadSize = 1F;
		this.lifetime = 1;
		this.hasPhysics = false;
		this.gravity = 0.0F;
	}

	@Override
	public void tick() {
		this.remove();
	}

	@Override
	@NotNull
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	@Environment(EnvType.CLIENT)
	public record DebugPosParticleFactory(@NotNull SpriteSet spriteProvider) implements ParticleProvider<SimpleParticleType> {
		@Override
		@NotNull
		public Particle createParticle(@NotNull SimpleParticleType defaultParticleType, @NotNull ClientLevel clientLevel, double x, double y, double z, double g, double h, double i) {
			return new DebugPosParticle(clientLevel, this.spriteProvider, x, y, z);
		}
	}
}
