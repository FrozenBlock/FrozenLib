package net.frozenblock.lib.levelgen.feature.api.treedecorators;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.config.v2.entry.predicates.ConfigPredicate;
import net.frozenblock.lib.levelgen.feature.impl.treedecorators.FrozenLibTreeDecoratorTypes;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

public class ConfigPredicateDecorator extends TreeDecorator {
	public static final MapCodec<ConfigPredicateDecorator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		TreeDecorator.CODEC.fieldOf("decorator").forGetter(decorator -> decorator.decorator),
		ConfigPredicate.CODEC.fieldOf("predicate").forGetter(decorator -> decorator.predicate)
	).apply(instance, ConfigPredicateDecorator::new));
	private final TreeDecorator decorator;
	private final ConfigPredicate predicate;

	public ConfigPredicateDecorator(TreeDecorator decorator, ConfigPredicate predicate) {
		this.decorator = decorator;
		this.predicate = predicate;
	}

	@Override
	protected TreeDecoratorType<?> type() {
		return FrozenLibTreeDecoratorTypes.CONFIG_PREDICATE.get();
	}

	@Override
	public void place(Context context) {
		if (this.predicate.test()) this.decorator.place(context);
	}
}
