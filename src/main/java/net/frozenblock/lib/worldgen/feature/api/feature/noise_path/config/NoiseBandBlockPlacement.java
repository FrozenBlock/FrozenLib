/*
 * Copyright (C) 2025 FrozenBlock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.worldgen.feature.api.feature.noise_path.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class NoiseBandBlockPlacement {
	public static final Codec<NoiseBandBlockPlacement> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
			BlockStateProvider.CODEC.fieldOf("state_provider").forGetter(config -> config.blockStateProvider),
			Codec.doubleRange(-1D, 1D).lenientOptionalFieldOf("minimum_noise_threshold", 1D).forGetter(config -> config.minNoiseThreshold),
			Codec.doubleRange(-1D, 1D).lenientOptionalFieldOf("maximum_noise_threshold", 1D).forGetter(config -> config.maxNoiseThreshold),
			Codec.floatRange(0F, 1F).lenientOptionalFieldOf("placement_chance", 1F).forGetter(config -> config.placementChance),
			Codec.BOOL.lenientOptionalFieldOf("schedule_tick_on_placement", false).forGetter(config -> config.scheduleTickOnPlacement),
			BlockPredicate.CODEC.fieldOf("replacement_block_predicate").forGetter(config -> config.replacementPredicate),
			BlockPredicate.CODEC.fieldOf("searching_block_predicate").forGetter(config -> config.searchingPredicate),
			Codec.INT.lenientOptionalFieldOf("vertical_placement_offset", 0).forGetter(config -> config.verticalPlacementOffset)
		).apply(instance, NoiseBandBlockPlacement::new)
	);

	private final BlockStateProvider blockStateProvider;
	private final double minNoiseThreshold;
	private final double maxNoiseThreshold;
	private final float placementChance;
	private final boolean scheduleTickOnPlacement;
	private final BlockPredicate replacementPredicate;
	private final BlockPredicate searchingPredicate;
	private final int verticalPlacementOffset;

	public NoiseBandBlockPlacement(
		BlockStateProvider stateProvider,
		double minNoiseThreshold,
		double maxNoiseThreshold,
		float placementChance,
		boolean scheduleTickOnPlacement,
		BlockPredicate replacementPredicate,
		BlockPredicate searchingPredicate,
		int verticalPlacementOffset
	) {
		this.blockStateProvider = stateProvider;
		this.minNoiseThreshold = minNoiseThreshold;
		this.maxNoiseThreshold = maxNoiseThreshold;
		this.placementChance = placementChance;
		this.scheduleTickOnPlacement = scheduleTickOnPlacement;
		this.replacementPredicate = replacementPredicate;
		this.searchingPredicate = searchingPredicate;
		this.verticalPlacementOffset = verticalPlacementOffset;
	}

	public boolean generate(
		WorldGenLevel level,
		BlockPos.MutableBlockPos pos,
		RandomSource random,
		double sampleOutput
	) {
		if (sampleOutput < this.minNoiseThreshold || sampleOutput > this.maxNoiseThreshold) return false;
		if (random.nextFloat() > this.placementChance) return false;

		pos.move(0, this.verticalPlacementOffset, 0);
		if (!this.replacementPredicate.test(level, pos)) return false;
		if (!this.searchingPredicate.test(level, pos)) return false;

		final BlockState state = this .blockStateProvider.getState(random, pos);
		level.setBlock(pos, state, Block.UPDATE_CLIENTS);
		if (this.scheduleTickOnPlacement) level.scheduleTick(pos, state.getBlock(), 1);
		return true;
	}

	public static class Builder {
		private final BlockStateProvider stateProvider;
		private double minNoiseThreshold = -1D;
		private double maxNoiseThreshold = 1D;
		private float placementChance = 1F;
		private boolean scheduleTickOnPlacement = false;
		private BlockPredicate replacementPredicate;
		private BlockPredicate searchingPredicate;
		private int verticalPlacementOffset = 0;

		public Builder(BlockStateProvider stateProvider) {
			this.stateProvider = stateProvider;
		}

		public Builder minNoiseThreshold(double minNoiseThreshold) {
			this.minNoiseThreshold = minNoiseThreshold;
			return this;
		}

		public Builder maxNoiseThreshold(double maxNoiseThreshold) {
			this.maxNoiseThreshold = maxNoiseThreshold;
			return this;
		}

		public Builder within(double minNoiseThreshold, double maxNoiseThreshold) {
			this.minNoiseThreshold = minNoiseThreshold;
			this.maxNoiseThreshold = maxNoiseThreshold;
			return this;
		}

		public Builder placementChance(float placementChance) {
			this.placementChance = placementChance;
			return this;
		}

		public Builder scheduleTickOnPlacement() {
			this.scheduleTickOnPlacement = true;
			return this;
		}

		public Builder replacementPredicate(BlockPredicate replacementPredicate) {
			this.replacementPredicate = replacementPredicate;
			return this;
		}

		public Builder searchingPredicate(BlockPredicate searchingPredicate) {
			this.searchingPredicate = searchingPredicate;
			return this;
		}

		public Builder verticalPlacementOffset(int verticalPlacementOffset) {
			this.verticalPlacementOffset = verticalPlacementOffset;
			return this;
		}

		public NoiseBandBlockPlacement build() {
			if (this.searchingPredicate == null) throw new IllegalArgumentException("searchingPredicate cannot be null for NoiseBandBlockPlacement!");
			if (this.replacementPredicate == null) throw new IllegalArgumentException("replacementPredicate cannot be null for NoiseBandBlockPlacement!");
			return new NoiseBandBlockPlacement(
				this.stateProvider,
				this.minNoiseThreshold,
				this.maxNoiseThreshold,
				this.placementChance,
				this.scheduleTickOnPlacement,
				this.replacementPredicate,
				this.searchingPredicate,
				this.verticalPlacementOffset
			);
		}
	}
}
