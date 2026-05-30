package net.frozenblock.lib.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.particle.options.ColoredSmokeParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.LargeSmokeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

@Environment(EnvType.CLIENT)
public class ColoredLargeSmokeParticle extends LargeSmokeParticle {
	private final float targetRColor;
	private final float startRColor;
	private final float targetBColor;
	private final float startBColor;
	private final float targetGColor;
	private final float startGColor;
	private final int colorLerpEndsAt;
	private int colorLerpTicks;

	protected ColoredLargeSmokeParticle(
		ClientLevel level,
		double x, double y, double z,
		double xa, double ya, double za,
		float rDifference, float gDifference, float bDifference,
		SpriteSet spriteSet
	) {
		super(level, x, y, z, xa, ya, za, spriteSet);
		this.targetRColor = this.rCol;
		this.rCol = this.startRColor = Math.clamp(this.targetRColor + rDifference, 0F, 1F);
		this.targetGColor = this.gCol;
		this.gCol = this.startGColor = Math.clamp(this.targetGColor + gDifference, 0F, 1F);
		this.targetBColor = this.bCol;
		this.bCol = this.startBColor = Math.clamp(this.targetBColor + bDifference, 0F, 1F);
		this.colorLerpEndsAt = this.lifetime / 2;
	}

	@Override
	public void tick() {
		this.colorLerpTicks += 1;
		super.tick();
	}

	@Override
	protected int getLightCoords(float partialTick) {
		final float colorLerp = Math.min((this.colorLerpTicks + partialTick), this.colorLerpEndsAt) / this.colorLerpEndsAt;
		this.rCol = Mth.lerp(colorLerp, this.startRColor, this.targetRColor);
		this.gCol = Mth.lerp(colorLerp, this.startGColor, this.targetGColor);
		this.bCol = Mth.lerp(colorLerp, this.startBColor, this.targetBColor);
		return super.getLightCoords(partialTick);
	}

	public record Provider(SpriteSet spriteSet) implements ParticleProvider<ColoredSmokeParticleOptions> {
		@Override
		public Particle createParticle(
			ColoredSmokeParticleOptions options,
			ClientLevel level,
			double x, double y, double z,
			double xAux, double yAux, double zAux,
			RandomSource random
		) {
			return new ColoredLargeSmokeParticle(
				level,
				x, y, z,
				xAux, yAux, zAux,
				options.rDifference(), options.gDifference(), options.bDifference(),
				this.spriteSet
			);
		}
	}
}
