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

package net.frozenblock.lib.levelgen.music.pitch.provider;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.loot.providers.number.UnaryProvider;

public record SquareRoot(Holder<PitchProvider> input) implements PitchProvider, UnaryProvider<PitchProvider> {
	public static final MapCodec<SquareRoot> MAP_CODEC = UnaryProvider.codec(HOLDER_CODEC, SquareRoot::new);

	@Override
	public MapCodec<? extends PitchProvider> codec() {
		return MAP_CODEC;
	}

	@Override
	public Holder<PitchProvider> input() {
		return this.input;
	}

	@Override
	public float sample(Context context) {
		return Mth.sqrt(this.input.value().sample(context));
	}

	@Override
	public boolean applicable(Context context) {
		return this.input.value().applicable(context);
	}
}
