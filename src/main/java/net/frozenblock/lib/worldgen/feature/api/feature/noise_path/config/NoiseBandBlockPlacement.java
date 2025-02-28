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
			BlockStateProvider.CODEC
				.fieldOf("state_provider")
				.forGetter(config -> config.blockStateProvider),
			Codec.doubleRange(-1D, 1D)
				.lenientOptionalFieldOf("minimum_noise_threshold", 1D)
				.forGetter(config -> config.minNoiseThreshold),
			Codec.doubleRange(-1D, 1D)
				.lenientOptionalFieldOf("maximum_noise_threshold", 1D)
				.forGetter(config -> config.maxNoiseThreshold),
			Codec.floatRange(0F, 1F)
				.lenientOptionalFieldOf("placement_chance", 1F)
				.forGetter(config -> config.placementChance),
			Codec.BOOL
				.lenientOptionalFieldOf("schedule_tick_on_placement", false)
				.forGetter(config -> config.scheduleTickOnPlacement),
			BlockPredicate.CODEC
				.fieldOf("replacement_block_predicate")
				.forGetter(config -> config.replacementBlockPredicate),
			BlockPredicate.CODEC
				.fieldOf("searching_block_predicate")
				.forGetter(config -> config.searchingBlockPredicate)
		).apply(instance, NoiseBandBlockPlacement::new)
	);

	private final BlockStateProvider blockStateProvider;
	private final double minNoiseThreshold;
	private final double maxNoiseThreshold;
	private final float placementChance;
	private final boolean scheduleTickOnPlacement;
	private final BlockPredicate replacementBlockPredicate;
	private final BlockPredicate searchingBlockPredicate;

	public NoiseBandBlockPlacement(
		BlockStateProvider blockStateProvider,
		double minNoiseThreshold,
		double maxNoiseThreshold,
		float placementChance,
		boolean scheduleTickOnPlacement,
		BlockPredicate replacementBlockPredicate,
		BlockPredicate searchingBlockPredicate
	) {
		this.blockStateProvider = blockStateProvider;
		this.minNoiseThreshold = minNoiseThreshold;
		this.maxNoiseThreshold = maxNoiseThreshold;
		this.placementChance = placementChance;
		this.scheduleTickOnPlacement = scheduleTickOnPlacement;
		this.replacementBlockPredicate = replacementBlockPredicate;
		this.searchingBlockPredicate = searchingBlockPredicate;
	}

	public boolean generate(
		WorldGenLevel level,
		BlockPos pos,
		RandomSource random,
		double sampleOutput
	) {
		if (sampleOutput >= this.minNoiseThreshold && sampleOutput <= this.maxNoiseThreshold) {
			if (random.nextFloat() <= this.placementChance) {
				if (this.replacementBlockPredicate.test(level, pos)) {
					if (this.searchingBlockPredicate.test(level, pos)) {
						BlockState state = this.blockStateProvider.getState(random, pos);
						level.setBlock(pos, state, Block.UPDATE_CLIENTS);
						if (this.scheduleTickOnPlacement) level.scheduleTick(pos, state.getBlock(), 1);
						return true;
					}
				}
			}
		}
		return false;
	}

	public static class Builder {
		private final BlockStateProvider blockStateProvider;
		private double minNoiseThreshold = -1D;
		private double maxNoiseThreshold = 1D;
		private float placementChance = 1F;
		private boolean scheduleTickOnPlacement = false;
		private BlockPredicate replacementBlockPredicate;
		private BlockPredicate searchingBlockPredicate;

		public Builder(BlockStateProvider blockStateProvider) {
			this.blockStateProvider = blockStateProvider;
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

		public Builder replacementBlockPredicate(BlockPredicate replacementBlockPredicate) {
			this.replacementBlockPredicate = replacementBlockPredicate;
			return this;
		}

		public Builder searchingBlockPredicate(BlockPredicate searchingBlockPredicate) {
			this.searchingBlockPredicate = searchingBlockPredicate;
			return this;
		}

		public NoiseBandBlockPlacement build() {
			if (this.searchingBlockPredicate == null) throw new IllegalArgumentException("searchingBlockPredicate cannot be null for NoiseBandBlockPlacement!");
			if (this.replacementBlockPredicate == null) throw new IllegalArgumentException("replacementBlockPredicate cannot be null for NoiseBandBlockPlacement!");
			return new NoiseBandBlockPlacement(
				this.blockStateProvider,
				this.minNoiseThreshold,
				this.maxNoiseThreshold,
				this.placementChance,
				this.scheduleTickOnPlacement,
				this.replacementBlockPredicate,
				this.searchingBlockPredicate
			);
		}
	}
}
