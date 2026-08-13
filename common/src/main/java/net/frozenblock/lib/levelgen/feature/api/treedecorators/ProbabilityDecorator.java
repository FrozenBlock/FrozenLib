package net.frozenblock.lib.levelgen.feature.api.treedecorators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.levelgen.feature.impl.treedecorators.FrozenLibTreeDecoratorTypes;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

public class ProbabilityDecorator extends TreeDecorator {
	public static final MapCodec<ProbabilityDecorator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		TreeDecorator.CODEC.fieldOf("decorator").forGetter(decorator -> decorator.decorator),
		Codec.FLOAT.fieldOf("probability").forGetter(decorator -> decorator.probability)
	).apply(instance, ProbabilityDecorator::new));
	private final TreeDecorator decorator;
	private final float probability;

	public ProbabilityDecorator(TreeDecorator decorator, float probability) {
		this.decorator = decorator;
		this.probability = probability;
	}

	@Override
	protected TreeDecoratorType<?> type() {
		return FrozenLibTreeDecoratorTypes.PROBABILITY.get();
	}

	@Override
	public void place(Context context) {
		if (context.random().nextFloat() < this.probability) this.decorator.place(context);
	}
}
