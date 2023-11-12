package net.frozenblock.lib.gravity.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public record GravityBelt<T extends GravityFunction>(double minY, double maxY, boolean renderBottom, boolean renderTop,
													 T function) {
	public GravityBelt(double minY, double maxY, T function) {
		this(minY, maxY, false, false, function);
	}

	public boolean affectsPosition(double y) {
		return y >= minY && y < maxY;
	}

	double getGravity(@Nullable Entity entity, double y) {
		if (this.affectsPosition(y)) {
            return this.function.get(entity, y, this.minY, this.maxY);
		}
		return 1.0;
	}

	public static <T extends SerializableGravityFunction<T>> Codec<GravityBelt<T>> codec(Codec<T> gravityFunction) {
		return RecordCodecBuilder.create(instance ->
			instance.group(
				Codec.DOUBLE.fieldOf("minY").forGetter(GravityBelt::minY),
				Codec.DOUBLE.fieldOf("maxY").forGetter(GravityBelt::maxY),
				gravityFunction.fieldOf("gravityFunction").forGetter(GravityBelt::function)
			).apply(instance, GravityBelt::new)
		);
	}

	@Nullable
	public static <T extends SerializableGravityFunction<T>> Codec<GravityBelt<T>> codec(T gravityFunction) {
		Codec<T> codec = gravityFunction.codec();
		if (codec == null) return null;
		return codec(codec);
	}
}
