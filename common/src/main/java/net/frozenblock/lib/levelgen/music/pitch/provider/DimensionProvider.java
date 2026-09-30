package net.frozenblock.lib.levelgen.music.pitch.provider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.resources.Identifier;

public record DimensionProvider(List<Identifier> requiredDimensions, PitchProvider provider) implements PitchProvider {
	public static final MapCodec<DimensionProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Identifier.CODEC.listOf().fieldOf("required_dimensions").forGetter(DimensionProvider::requiredDimensions),
		DIRECT_CODEC.fieldOf("provider").forGetter(DimensionProvider::provider)
	).apply(instance, DimensionProvider::new));

	@Override
	public float sample(long gameTime) {
		return this.provider.sample(gameTime);
	}

	@Override
	public boolean applicable(Context context) {
		return this.requiredDimensions.contains(context.dimension()) && this.provider.applicable(context);
	}

	@Override
	public MapCodec<DimensionProvider> codec() {
		return CODEC;
	}
}
