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
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;

public record Lerp(Holder<PitchProvider> input, Holder<PitchProvider> start, Holder<PitchProvider> end) implements PitchProvider {
	public static MapCodec<Lerp> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		HOLDER_CODEC.fieldOf("input").forGetter(Lerp::input),
		HOLDER_CODEC.fieldOf("start").forGetter(Lerp::start),
		HOLDER_CODEC.fieldOf("end").forGetter(Lerp::end)
	).apply(instance, Lerp::new));

	@Override
	public float sample(Context context) {
		return Mth.lerp(this.input.value().sample(context), this.start.value().sample(context), this.end.value().sample(context));
	}

	@Override
	public boolean applicable(Context context) {
		return this.input.value().applicable(context) && this.start.value().applicable(context) && this.end.value().applicable(context);
	}

	@Override
	public MapCodec<Lerp> codec() {
		return MAP_CODEC;
	}

	@Override
	public void validate(ValidationContext context) {
		Validatable.validateHolder(context, "input", this.input);
		Validatable.validateHolder(context, "start", this.start);
		Validatable.validateHolder(context, "end", this.end);
	}
}
