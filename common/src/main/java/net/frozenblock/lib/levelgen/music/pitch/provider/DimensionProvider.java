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
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.resources.Identifier;

public record DimensionProvider(List<Identifier> requiredDimensions, PitchProvider provider) implements PitchProvider {
	public static final MapCodec<DimensionProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Identifier.CODEC.listOf().fieldOf("required_dimensions").forGetter(DimensionProvider::requiredDimensions),
		DIRECT_CODEC.fieldOf("provider").forGetter(DimensionProvider::provider)
	).apply(instance, DimensionProvider::new));

	@Override
	public float sample(long gameTime) {
		return this.provider.sample(gameTime);
	}

	@Override
	public boolean applicable(Context context) {
		return this.requiredDimensions.contains(context.dimension()) && this.provider.applicable(context);
	}

	@Override
	public MapCodec<DimensionProvider> codec() {
		return CODEC;
	}
}
