/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.levelgen.feature.api.treedecorators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.levelgen.feature.impl.treedecorators.FrozenLibTreeDecoratorTypes;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

public class ProbabilityDecorator extends TreeDecorator {
	public static final MapCodec<ProbabilityDecorator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		TreeDecorator.CODEC.fieldOf("decorator").forGetter(decorator -> decorator.decorator),
		Codec.FLOAT.fieldOf("probability").forGetter(decorator -> decorator.probability)
	).apply(instance, ProbabilityDecorator::new));
	private final TreeDecorator decorator;
	private final float probability;

	public ProbabilityDecorator(TreeDecorator decorator, float probability) {
		this.decorator = decorator;
		this.probability = probability;
	}

	@Override
	protected TreeDecoratorType<?> type() {
		return FrozenLibTreeDecoratorTypes.PROBABILITY.get();
	}

	@Override
	public void place(Context context) {
		if (context.random().nextFloat() < this.probability) this.decorator.place(context);
	}
}
