package net.frozenblock.lib.levelgen.feature.api.stateproviders;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.RuleBasedStateProvider;
import org.jspecify.annotations.Nullable;

/**
 * An alternate implementation of {@link RuleBasedStateProvider} that returns {@link BlockStateProvider#getOptionalState(LevelAccessor, RandomSource, BlockPos) getOptionalState}
 * instead of {@link BlockStateProvider#getState(LevelAccessor, RandomSource, BlockPos) getState} on rule failure.
 * <p>
 * This implementation fixes an issue that causes nested {@link RuleBasedStateProvider}s to be considered successful even when failing.
 */
public record StrictRuleBasedStateProvider(List<RuleBasedStateProvider.Rule> rules) implements BlockStateProvider {
	public static final MapCodec<StrictRuleBasedStateProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		RuleBasedStateProvider.Rule.CODEC.listOf().fieldOf("rules").forGetter(provider -> provider.rules)
	).apply(instance, StrictRuleBasedStateProvider::new));

	public static StrictRuleBasedStateProvider ifTrueThenProvide(BlockPredicate ifTrue, Block thenProvide) {
		return ifTrueThenProvide(ifTrue, BlockStateProvider.simple(thenProvide));
	}

	public static StrictRuleBasedStateProvider ifTrueThenProvide(BlockPredicate ifTrue, BlockStateProvider thenProvide) {
		return new StrictRuleBasedStateProvider(List.of(new RuleBasedStateProvider.Rule(ifTrue, thenProvide)));
	}

	@Override
	public MapCodec<StrictRuleBasedStateProvider> codec() {
		return CODEC;
	}

	@Override
	public BlockState getState(LevelAccessor level, RandomSource random, BlockPos pos) {
		final BlockState result = this.getOptionalState(level, random, pos);
		return result != null ? result : level.getBlockState(pos);
	}

	@Override
	public @Nullable BlockState getOptionalState(LevelAccessor level, RandomSource random, BlockPos pos) {
		for (RuleBasedStateProvider.Rule rule : this.rules) {
			if (rule.ifTrue().test(level, pos)) return rule.then().getOptionalState(level, random, pos);
		}

		return null;
	}

	public static StrictRuleBasedStateProvider.Builder builder() {
		return new StrictRuleBasedStateProvider.Builder();
	}

	public static class Builder {
		private final List<RuleBasedStateProvider.Rule> rules = new ArrayList<>();

		private Builder() {}

		public StrictRuleBasedStateProvider.Builder ifTrueThenProvide(BlockPredicate ifTrue, BlockStateProvider thenProvide) {
			this.rules.add(new RuleBasedStateProvider.Rule(ifTrue, thenProvide));
			return this;
		}

		public StrictRuleBasedStateProvider.Builder ifTrueThenProvide(BlockPredicate ifTrue, Block thenProvide) {
			this.rules.add(new RuleBasedStateProvider.Rule(ifTrue, BlockStateProvider.simple(thenProvide)));
			return this;
		}

		public StrictRuleBasedStateProvider.Builder ifTrueThenProvide(BlockPredicate ifTrue, BlockState thenProvide) {
			this.rules.add(new RuleBasedStateProvider.Rule(ifTrue, BlockStateProvider.simple(thenProvide)));
			return this;
		}

		public StrictRuleBasedStateProvider build() {
			return new StrictRuleBasedStateProvider(this.rules);
		}
	}
}
