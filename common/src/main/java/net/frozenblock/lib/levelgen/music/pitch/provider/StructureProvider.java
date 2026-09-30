package net.frozenblock.lib.levelgen.music.pitch.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.frozenblock.lib.levelgen.structure.impl.status.StructureStatus;
import net.minecraft.resources.Identifier;

public record StructureProvider(List<Identifier> requiredStructures, boolean requireInsidePiece, PitchProvider provider) implements PitchProvider {
	public static final MapCodec<StructureProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Identifier.CODEC.listOf().fieldOf("required_structures").forGetter(StructureProvider::requiredStructures),
		Codec.BOOL.optionalFieldOf("require_inside_piece", true).forGetter(StructureProvider::requireInsidePiece),
		DIRECT_CODEC.fieldOf("provider").forGetter(StructureProvider::provider)
	).apply(instance, StructureProvider::new));

	@Override
	public float sample(long gameTime) {
		return this.provider.sample(gameTime);
	}

	@Override
	public boolean applicable(Context context) {
		if (context.structureStatus().isEmpty()) return false;

		final StructureStatus status = context.structureStatus().get();
		return status.insidePiece() == this.requireInsidePiece
			&& this.requiredStructures.contains(status.structure())
			&& this.provider.applicable(context);
	}

	@Override
	public MapCodec<StructureProvider> codec() {
		return CODEC;
	}
}
