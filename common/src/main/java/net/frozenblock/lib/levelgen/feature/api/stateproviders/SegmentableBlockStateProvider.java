/*
 * Copyright (C) 2024-2026 FrozenBlock
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

package net.frozenblock.lib.levelgen.feature.api.stateproviders;

import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SegmentableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public interface SegmentableBlockStateProvider {

	IntegerProperty segmentAmountProperty();

	Holder<Block> block();

	int minSegment();

	int maxSegment();

	default BlockState getState(RandomSource random) {
		return this.block().value().defaultBlockState()
			.trySetValue(this.segmentAmountProperty(), random.nextIntBetweenInclusive(this.minSegment(), this.maxSegment()));
	}

	static <T extends SegmentableBlockStateProvider> MapCodec<T> codec(Function3<Holder<Block>, Integer, Integer, T> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			RegistryCodecs.holder(Registries.BLOCK).fieldOf("block").forGetter(SegmentableBlockStateProvider::block),
			ExtraCodecs.intRange(SegmentableBlock.MIN_SEGMENT, SegmentableBlock.MAX_SEGMENT).optionalFieldOf("min_segment", SegmentableBlock.MIN_SEGMENT).forGetter(SegmentableBlockStateProvider::minSegment),
			ExtraCodecs.intRange(SegmentableBlock.MIN_SEGMENT, SegmentableBlock.MAX_SEGMENT).fieldOf("max_segment").forGetter(SegmentableBlockStateProvider::maxSegment)
		).apply(instance, constructor));
	}
}
