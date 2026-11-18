package net.frozenblock.lib.levelgen.feature.api.foliageplacers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.config.v2.entry.predicates.ConfigPredicate;
import net.frozenblock.lib.levelgen.feature.impl.foliageplacers.FrozenLibFoliagePlacerTypes;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

public class ConditionalFoliagePlacer extends FoliagePlacer {
	public static final MapCodec<ConditionalFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ConfigPredicate.HOLDER_CODEC.fieldOf("condition").forGetter(foliagePlacer -> foliagePlacer.condition),
		FoliagePlacer.CODEC.fieldOf("on_true").forGetter(foliagePlacer -> foliagePlacer.onTrue),
		FoliagePlacer.CODEC.fieldOf("on_false").forGetter(foliagePlacer -> foliagePlacer.onFalse)
	).apply(instance, ConditionalFoliagePlacer::new));
	private final Holder<ConfigPredicate> condition;
	private final FoliagePlacer onTrue;
	private final FoliagePlacer onFalse;

	public ConditionalFoliagePlacer(
		Holder<ConfigPredicate> condition,
		FoliagePlacer onTrue,
		FoliagePlacer onFalse
	) {
		super(ConstantInt.of(1), ConstantInt.of(0));
		this.condition = condition;
		this.onTrue = onTrue;
		this.onFalse = onFalse;
	}

	@Override
	protected FoliagePlacerType<?> type() {
		return FrozenLibFoliagePlacerTypes.CONDITIONAL_FOLIAGE_PLACER.get();
	}

	public void createFoliage(
		WorldGenLevel level,
		FoliagePlacer.FoliageSetter foliageSetter,
		RandomSource random,
		TreeFeature tree,
		int treeHeight,
		FoliagePlacer.FoliageAttachment foliageAttachment,
		int foliageHeight,
		int leafRadius
	) {
		this.getSelectedFoliagePlacer().createFoliage(level, foliageSetter, random, tree, treeHeight, foliageAttachment, foliageHeight, leafRadius);
	}

	@Override
	protected void createFoliage(
		WorldGenLevel level,
		FoliageSetter foliageSetter,
		RandomSource random,
		TreeFeature tree,
		int treeHeight,
		FoliageAttachment foliageAttachment,
		int foliageHeight,
		int leafRadius,
		int offset
	) {}

	public FoliagePlacer getSelectedFoliagePlacer() {
		return this.condition.value().test() ? this.onTrue : this.onFalse;
	}

	@Override
	public int foliageHeight(RandomSource random, int treeHeight, TreeFeature tree) {
		return this.getSelectedFoliagePlacer().foliageHeight(random, treeHeight, tree);
	}

	@Override
	public int foliageRadius(RandomSource random, int trunkHeight) {
		return this.getSelectedFoliagePlacer().foliageRadius(random, trunkHeight);
	}

	@Override
	private int offset(RandomSource random) {
		return this.offset.sample(random);
	}

	@Override
	protected boolean shouldSkipLocation(RandomSource random, int dx, int y, int dz, int currentRadius, boolean doubleTrunk) {
		return false;
	}
}
