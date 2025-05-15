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

package net.frozenblock.lib.worldgen.feature.api.feature.disk.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.jetbrains.annotations.NotNull;

public class BallOuterRingBlockPlacement {
	public static final Codec<BallOuterRingBlockPlacement> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
			BlockStateProvider.CODEC.fieldOf("state_provider").forGetter(config -> config.blockStateProvider),
			Codec.floatRange(0F, 1F).lenientOptionalFieldOf("placement_chance", 1F).forGetter(config -> config.placementChance),
			Codec.floatRange(0F, 1F).lenientOptionalFieldOf("outer_ring_start_percentage", 0F).forGetter(config -> config.outerRingStartPercentage),
			Codec.floatRange(0F, 1F).lenientOptionalFieldOf("chance_to_choose_in_inner_ring", 0F).forGetter(config -> config.chanceToChooseInInnerRing),
			BlockPredicate.CODEC.fieldOf("replacement_block_predicate").forGetter(config -> config.replacementBlockPredicate),
			BlockPredicate.CODEC.fieldOf("searching_block_predicate").forGetter(config -> config.searchingBlockPredicate),
			Codec.BOOL.lenientOptionalFieldOf("schedule_tick_on_placement", false).forGetter(config -> config.scheduleTickOnPlacement),
			Codec.INT.lenientOptionalFieldOf("vertical_placement_offset", 0).forGetter(config -> config.verticalPlacementOffset)
		).apply(instance, BallOuterRingBlockPlacement::new)
	);

	private final BlockStateProvider blockStateProvider;
	private final float placementChance;
	private final float outerRingStartPercentage;
	private final float chanceToChooseInInnerRing;
	private final BlockPredicate replacementBlockPredicate;
	private final BlockPredicate searchingBlockPredicate;
	private final boolean scheduleTickOnPlacement;
	private final int verticalPlacementOffset;

	public BallOuterRingBlockPlacement(
		BlockStateProvider blockStateProvider,
		float placementChance,
		float outerRingStartPercentage,
		float chanceToChooseInInnerRing,
		BlockPredicate replacementBlockPredicate,
		BlockPredicate searchingBlockPredicate,
		boolean scheduleTickOnPlacement,
		int verticalPlacementOffset
	) {
		this.blockStateProvider = blockStateProvider;
		this.placementChance = placementChance;
		this.outerRingStartPercentage = outerRingStartPercentage;
		this.chanceToChooseInInnerRing = chanceToChooseInInnerRing;
		this.replacementBlockPredicate = replacementBlockPredicate;
		this.searchingBlockPredicate = searchingBlockPredicate;
		this.scheduleTickOnPlacement = scheduleTickOnPlacement;
		this.verticalPlacementOffset = verticalPlacementOffset;
	}

	public float getPlacementChance() {
		return this.placementChance;
	}

	public OuterRingSelectionType chooseSelectionType(
		double distance,
		int placementRadius,
		float chanceToChooseInnerBlockInOuterRing,
		RandomSource random
	) {
		if (distance <= placementRadius * this.outerRingStartPercentage) {
			return random.nextFloat() <= chanceToChooseInnerBlockInOuterRing ? OuterRingSelectionType.FAIL : OuterRingSelectionType.SUCCESS;
		} else {
			return random.nextFloat() <= this.chanceToChooseInInnerRing ? OuterRingSelectionType.OUTER_IN_INNER : OuterRingSelectionType.FAIL;
		}
	}

	public boolean generate(
		WorldGenLevel level,
		BlockPos.MutableBlockPos pos,
		@NotNull RandomSource random,
		boolean isWithinOppositeRing
	) {
		if (isWithinOppositeRing || random.nextFloat() <= this.placementChance) {
			pos.move(0, this.verticalPlacementOffset, 0);
			if (this.replacementBlockPredicate.test(level, pos)) {
				if (this.searchingBlockPredicate.test(level, pos)) {
					BlockState state = this .blockStateProvider.getState(random, pos);
					level.setBlock(pos, state, Block.UPDATE_CLIENTS);
					if (this.scheduleTickOnPlacement) level.scheduleTick(pos, state.getBlock(), 1);
					return true;
				}
			}
		}
		return false;
	}

	public enum OuterRingSelectionType {
		SUCCESS,
		OUTER_IN_INNER,
		FAIL
	}

	public static class Builder {
		private final BlockStateProvider blockStateProvider;
		private float placementChance = 1F;
		private float outerRingStartPercentage = 0F;
		private float chanceToChooseInInnerRing = 0F;
		private BlockPredicate replacementBlockPredicate = BlockPredicate.replaceable();
		private BlockPredicate searchingBlockPredicate = BlockPredicate.alwaysTrue();
		private boolean scheduleTickOnPlacement = false;
		private int verticalPlacementOffset = 0;

		public Builder(BlockStateProvider blockStateProvider) {
			this.blockStateProvider = blockStateProvider;
		}

		public Builder placementChance(float chance) {
			this.placementChance = chance;
			return this;
		}

		public Builder outerRingStartPercentage(float percentage) {
			this.outerRingStartPercentage = percentage;
			return this;
		}

		public Builder chanceToChooseInInnerRing(float chance) {
			this.chanceToChooseInInnerRing = chance;
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

		public Builder scheduleTickOnPlacement() {
			this.scheduleTickOnPlacement = true;
			return this;
		}

		public Builder verticalPlacementOffset(int verticalPlacementOffset) {
			this.verticalPlacementOffset = verticalPlacementOffset;
			return this;
		}

		public BallOuterRingBlockPlacement build() {
			return new BallOuterRingBlockPlacement(
				this.blockStateProvider,
				this.placementChance,
				this.outerRingStartPercentage,
				this.chanceToChooseInInnerRing,
				this.replacementBlockPredicate,
				this.searchingBlockPredicate,
				this.scheduleTickOnPlacement,
				this.verticalPlacementOffset
			);
		}
	}
}
