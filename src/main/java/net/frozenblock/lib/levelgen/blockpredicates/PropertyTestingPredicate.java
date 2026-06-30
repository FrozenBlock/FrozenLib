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

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.blockpredicates.StateTestingPredicate;

public abstract class PropertyTestingPredicate<T extends Comparable<T>> extends StateTestingPredicate {
	protected final Property<T> property;
	protected final List<T> values;

	public PropertyTestingPredicate(Vec3i offset, Property<T> property, List<T> values) {
		super(offset);
		this.property = property;
		this.values = values;
	}

	public PropertyTestingPredicate(Vec3i offset, Property<T> property, T value) {
		this(offset, property, List.of(value));
	}

	protected static <V extends Comparable<V>, P extends PropertyTestingPredicate<V>> Products.P2<RecordCodecBuilder.Mu<P>, Vec3i, List<V>> propertyTestingCodec(
		RecordCodecBuilder.Instance<P> instance, Codec<V> valueCodec
	) {
		return stateTestingCodec(instance)
			.and(valueCodec.listOf().fieldOf("values").forGetter(predicate -> predicate.values));
	}

	@Override
	protected boolean test(BlockState state) {
		if (!state.hasProperty(this.property)) return false;
		return this.values.stream().anyMatch(value -> state.getValue(this.property) == value);
	}
}
