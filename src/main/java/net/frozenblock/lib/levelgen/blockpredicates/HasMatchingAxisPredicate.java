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

package net.frozenblock.lib.levelgen.blockpredicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.frozenblock.lib.levelgen.blockpredicates.impl.FrozenLibBlockPredicateTypes;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;

public class HasMatchingAxisPredicate extends PropertyTestingPredicate<Direction.Axis> {
	public static final MapCodec<HasMatchingAxisPredicate> CODEC = RecordCodecBuilder.mapCodec(instance ->
		propertyTestingCodec(instance, Direction.Axis.CODEC)
		.apply(instance, HasMatchingAxisPredicate::new)
	);

	public HasMatchingAxisPredicate(Vec3i offset, List<Direction.Axis> values) {
		super(offset, BlockStateProperties.AXIS, values);
	}

	public HasMatchingAxisPredicate(Vec3i offset, Direction.Axis value) {
		super(offset, BlockStateProperties.AXIS, value);
	}

	public static HasMatchingAxisPredicate of(Direction.Axis value) {
		return new HasMatchingAxisPredicate(Vec3i.ZERO, value);
	}

	@Override
	public BlockPredicateType<?> type() {
		return FrozenLibBlockPredicateTypes.HAS_MATCHING_AXIS;
	}
}
