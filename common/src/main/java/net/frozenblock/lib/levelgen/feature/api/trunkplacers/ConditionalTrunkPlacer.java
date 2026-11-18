package net.frozenblock.lib.levelgen.feature.api.trunkplacers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.BiConsumer;
import net.frozenblock.lib.config.v2.entry.predicates.ConfigPredicate;
import net.frozenblock.lib.levelgen.feature.impl.trunkplacers.FrozenLibTrunkPlacerTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

public class ConditionalTrunkPlacer extends TrunkPlacer {
	public static final MapCodec<ConditionalTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ConfigPredicate.HOLDER_CODEC.fieldOf("condition").forGetter(trunkPlacer -> trunkPlacer.condition),
		TrunkPlacer.CODEC.fieldOf("on_true").forGetter(trunkPlacer -> trunkPlacer.onTrue),
		TrunkPlacer.CODEC.fieldOf("on_false").forGetter(trunkPlacer -> trunkPlacer.onFalse)
	).apply(instance, ConditionalTrunkPlacer::new));
	private final Holder<ConfigPredicate> condition;
	private final TrunkPlacer onTrue;
	private final TrunkPlacer onFalse;

	public ConditionalTrunkPlacer(
		Holder<ConfigPredicate> condition,
		TrunkPlacer onTrue,
		TrunkPlacer onFalse
	) {
		super(1, 0, 0);
		this.condition = condition;
		this.onTrue = onTrue;
		this.onFalse = onFalse;
	}

	@Override
	protected TrunkPlacerType<?> type() {
		return FrozenLibTrunkPlacerTypes.CONDITIONAL_TRUNK_PLACER.get();
	}

	public TrunkPlacer getSelectedTrunkPlacer() {
		return this.condition.value().test() ? this.onTrue : this.onFalse;
	}

	@Override
	public int getBaseHeight() {
		return this.getSelectedTrunkPlacer().getBaseHeight();
	}

	@Override
	public int getTreeHeight(RandomSource random) {
		return this.getSelectedTrunkPlacer().getTreeHeight(random);
	}

	@Override
	public List<FoliagePlacer.FoliageAttachment> placeTrunk(
		WorldGenLevel level,
		BiConsumer<BlockPos, BlockState> trunkSetter,
		RandomSource random,
		int treeHeight,
		BlockPos origin,
		TreeFeature tree
	) {
		return this.getSelectedTrunkPlacer().placeTrunk(level, trunkSetter, random, treeHeight, origin, tree);
	}
}
