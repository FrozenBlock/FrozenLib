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
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;

public record DimensionRequirement(List<Identifier> requiredDimensions, Holder<PitchProvider> provider) implements PitchProvider {
	public static final MapCodec<DimensionRequirement> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Identifier.CODEC.listOf().fieldOf("requirements").forGetter(DimensionRequirement::requiredDimensions),
		HOLDER_CODEC.fieldOf("provider").forGetter(DimensionRequirement::provider)
	).apply(instance, DimensionRequirement::new));

	@Override
	public float sample(Context context) {
		return this.provider.value().sample(context);
	}

	@Override
	public boolean applicable(Context context) {
		return this.requiredDimensions.contains(context.dimension()) && this.provider.value().applicable(context);
	}

	@Override
	public MapCodec<DimensionRequirement> codec() {
		return MAP_CODEC;
	}

	@Override
	public void validate(ValidationContext context) {
		Validatable.validateHolder(context, "provider", this.provider);
	}
}
