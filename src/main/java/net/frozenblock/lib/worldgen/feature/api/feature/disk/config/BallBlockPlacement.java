/*
 * Copyright (C) 2025-2026 FrozenBlock
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
import java.util.Optional;
import net.frozenblock.lib.math.api.AdvancedMath;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class BallBlockPlacement {
	public static final Codec<BallBlockPlacement> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
			BlockStateProvider.CODEC.fieldOf("state_provider").forGetter(config -> config.stateProvider),
			Codec.floatRange(0F, 1F).lenientOptionalFieldOf("placement_chance", 1F).forGetter(config -> config.placementChance),
			Codec.floatRange(0F, 1F).lenientOptionalFieldOf("chance_to_choose_inner_block_in_outer_ring", 0F).forGetter(config -> config.chanceToChooseInnerBlockInOuterRing),
			Codec.floatRange(0F, 1F).lenientOptionalFieldOf("fade_start_percentage", 1F).forGetter(config -> config.fadeStartPercentage),
			BlockPredicate.CODEC.fieldOf("replacement_block_predicate").forGetter(config -> config.replacementBlockPredicate),
			BlockPredicate.CODEC.fieldOf("searching_block_predicate").forGetter(config -> config.searchingBlockPredicate),
			Codec.BOOL.lenientOptionalFieldOf("schedule_tick_on_placement", false).forGetter(config -> config.scheduleTickOnPlacement),
			Codec.INT.lenientOptionalFieldOf("vertical_placement_offset", 0).forGetter(config -> config.verticalPlacementOffset),
			RegistryCodecs.homogeneousList(Registries.BIOME).lenientOptionalFieldOf("excluded_biomes").forGetter(config -> config.excludedBiomes),
			BallOuterRingBlockPlacement.CODEC.lenientOptionalFieldOf("outer_placement").forGetter(config -> config.outerRingBlockPlacement)
		).apply(instance, BallBlockPlacement::new)
	);

	private final BlockStateProvider stateProvider;
	private final float placementChance;
	private final float chanceToChooseInnerBlockInOuterRing;
	private final float fadeStartPercentage;
	private final BlockPredicate replacementBlockPredicate;
	private final BlockPredicate searchingBlockPredicate;
	private final boolean scheduleTickOnPlacement;
	private final int verticalPlacementOffset;
	private final Optional<HolderSet<Biome>> excludedBiomes;
	private final Optional<BallOuterRingBlockPlacement> outerRingBlockPlacement;

	public BallBlockPlacement(
		BlockStateProvider stateProvider,
		float placementChance,
		float chanceToChooseInnerBlockInOuterRing,
		float fadeStartPercentage,
		BlockPredicate replacementPredicate,
		BlockPredicate searchingPredicate,
		boolean scheduleTickOnPlacement,
		int verticalPlacementOffset,
		Optional<HolderSet<Biome>> excludedBiomes,
		Optional<BallOuterRingBlockPlacement> outerRingBlockPlacement
	) {
		this.stateProvider = stateProvider;
		this.placementChance = placementChance;
		this.chanceToChooseInnerBlockInOuterRing = chanceToChooseInnerBlockInOuterRing;
		this.fadeStartPercentage = fadeStartPercentage;
		this.replacementBlockPredicate = replacementPredicate;
		this.searchingBlockPredicate = searchingPredicate;
		this.scheduleTickOnPlacement = scheduleTickOnPlacement;
		this.verticalPlacementOffset = verticalPlacementOffset;
		this.excludedBiomes = excludedBiomes;
		this.outerRingBlockPlacement = outerRingBlockPlacement;
	}

	public boolean generate(
		WorldGenLevel level,
		BlockPos center,
		BlockPos.MutableBlockPos pos,
		boolean usingHeightmap,
		int placementRadius,
		RandomSource random
	) {
		final double distance = AdvancedMath.distanceBetween(center, pos, !usingHeightmap);
		if (distance > placementRadius) return false;

		final boolean fading = distance >= placementRadius * this.fadeStartPercentage;
		if (fading && random.nextBoolean()) return false;

		if (this.excludedBiomes.isPresent() && this.excludedBiomes.get().contains(level.getBiome(pos))) return false;
		if (this.outerRingBlockPlacement.isPresent()) {
			final BallOuterRingBlockPlacement outerRingBlockPlacement = this.outerRingBlockPlacement.get();
			final BallOuterRingBlockPlacement.OuterRingSelectionType selectionType = outerRingBlockPlacement.chooseSelectionType(
				distance, placementRadius, this.chanceToChooseInnerBlockInOuterRing, random
			);

			if (selectionType == BallOuterRingBlockPlacement.OuterRingSelectionType.OUTER_IN_INNER) {
				if (random.nextFloat() <= outerRingBlockPlacement.getPlacementChance()) return outerRingBlockPlacement.generate(level, pos, random, true);
			} else if (selectionType == BallOuterRingBlockPlacement.OuterRingSelectionType.SUCCESS) {
				return outerRingBlockPlacement.generate(level, pos, random, false);
			}
		}

		if (random.nextFloat() <= this.placementChance) return this.generateBlock(level, pos, random);
		return false;
	}

	public boolean generateBlock(WorldGenLevel level, BlockPos.MutableBlockPos pos, RandomSource random) {
		pos.move(0, this.verticalPlacementOffset, 0);
		if (!this.replacementBlockPredicate.test(level, pos)) return false;
		if (!this.searchingBlockPredicate.test(level, pos)) return false;

		final BlockState state = this.stateProvider.getState(random, pos);
		level.setBlock(pos, state, Block.UPDATE_CLIENTS);
		if (this.scheduleTickOnPlacement) level.scheduleTick(pos, state.getBlock(), 1);
		return true;
	}

	public static class Builder {
		private final BlockStateProvider stateProvider;
		private float placementChance = 1F;
		private float chanceToChooseInnerBlockInOuterRing = 0F;
		private float fadeStartPercentage = 1F;
		private BlockPredicate replacementPredicate = BlockPredicate.replaceable();
		private BlockPredicate searchingPredicate = BlockPredicate.alwaysTrue();
		private boolean scheduleTickOnPlacement = false;
		private int verticalPlacementOffset = 0;
		private Optional<HolderSet<Biome>> excludedBiomes = Optional.empty();
		private Optional<BallOuterRingBlockPlacement> outerRingBlockPlacement = Optional.empty();

		public Builder(BlockStateProvider stateProvider) {
			this.stateProvider = stateProvider;
		}

		public Builder placementChance(float chance) {
			this.placementChance = chance;
			return this;
		}

		public Builder chanceToChooseInnerBlockInOuterRing(float chance) {
			this.chanceToChooseInnerBlockInOuterRing = chance;
			return this;
		}

		public Builder fadeStartPercentage(float fadeStartPercentage) {
			this.fadeStartPercentage = fadeStartPercentage;
			return this;
		}

		public Builder replacementBlockPredicate(BlockPredicate replacementPredicate) {
			this.replacementPredicate = replacementPredicate;
			return this;
		}

		public Builder searchingBlockPredicate(BlockPredicate searchingPredicate) {
			this.searchingPredicate = searchingPredicate;
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

		public Builder excludedBiomes(HolderSet<Biome> excludedBiomes) {
			this.excludedBiomes = Optional.of(excludedBiomes);
			return this;
		}

		public Builder outerRingBlockPlacement(BallOuterRingBlockPlacement outerRingBlockPlacement) {
			this.outerRingBlockPlacement = Optional.of(outerRingBlockPlacement);
			return this;
		}

		public BallBlockPlacement build() {
			return new BallBlockPlacement(
				this.stateProvider,
				this.placementChance,
				this.chanceToChooseInnerBlockInOuterRing,
				this.fadeStartPercentage,
				this.replacementPredicate,
				this.searchingPredicate,
				this.scheduleTickOnPlacement,
				this.verticalPlacementOffset,
				this.excludedBiomes,
				this.outerRingBlockPlacement
			);
		}
	}
}
