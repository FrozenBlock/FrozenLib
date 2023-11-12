package net.frozenblock.lib.gravity.api.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.gravity.api.GravityBelt;
import net.frozenblock.lib.gravity.api.SerializableGravityFunction;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public record AbsoluteGravityFunction(double gravity) implements SerializableGravityFunction<AbsoluteGravityFunction> {

	public static final Codec<AbsoluteGravityFunction> CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
			Codec.DOUBLE.fieldOf("gravity").forGetter(AbsoluteGravityFunction::gravity)
		).apply(instance, AbsoluteGravityFunction::new)
	);

	public static final Codec<GravityBelt<AbsoluteGravityFunction>> BELT_CODEC = GravityBelt.codec(CODEC);

	@Override
	public double get(@Nullable Entity entity, double y, double minY, double maxY) {
		return gravity();
	}

	@Override
	public Codec<AbsoluteGravityFunction> codec() {
		return CODEC;
	}
}
