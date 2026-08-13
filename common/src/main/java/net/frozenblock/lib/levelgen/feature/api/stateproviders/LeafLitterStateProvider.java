package net.frozenblock.lib.levelgen.feature.api.stateproviders;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeafLitterBlock;
import net.minecraft.world.level.block.SegmentableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

/**
 * Provides all {@link BlockState}s of {@link LeafLitterBlock}s.
 * @param defaultState The default {@link BlockState} of the {@link LeafLitterBlock}.
 * @param minSegment The minimum {@link LeafLitterBlock#AMOUNT segment amount}.
 * <p>
 * Defaults to 1.
 * @param maxSegment The maximum {@link LeafLitterBlock#AMOUNT segment amount}.
 */
public record LeafLitterStateProvider(BlockState defaultState, int minSegment, int maxSegment) implements BlockStateProvider {
	public static final MapCodec<LeafLitterStateProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockState.CODEC.fieldOf("block").forGetter(LeafLitterStateProvider::defaultState),
		ExtraCodecs.intRange(SegmentableBlock.MIN_SEGMENT, SegmentableBlock.MAX_SEGMENT).optionalFieldOf("min_segment", SegmentableBlock.MIN_SEGMENT).forGetter(LeafLitterStateProvider::minSegment),
		ExtraCodecs.intRange(SegmentableBlock.MIN_SEGMENT, SegmentableBlock.MAX_SEGMENT).fieldOf("max_segment").forGetter(LeafLitterStateProvider::maxSegment)
	).apply(instance, LeafLitterStateProvider::new));

	public LeafLitterStateProvider(Block block, int minSegment, int maxSegment) {
		this(block.defaultBlockState(), minSegment, maxSegment);
	}

	public LeafLitterStateProvider(Block block, int maxSegment) {
		this(block, 1, maxSegment);
	}

	@Override
	public MapCodec<LeafLitterStateProvider> codec() {
		return CODEC;
	}

	@Override
	public BlockState getState(LevelAccessor level, RandomSource random, BlockPos pos) {
		return this.defaultState
			.trySetValue(LeafLitterBlock.AMOUNT, random.nextIntBetweenInclusive(this.minSegment, this.maxSegment))
			.trySetValue(LeafLitterBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(random));
	}
}
