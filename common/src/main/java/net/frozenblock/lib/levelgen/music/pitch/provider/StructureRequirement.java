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

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.frozenblock.lib.levelgen.structure.impl.status.StructureStatus;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;

public record StructureRequirement(List<Pair<Identifier, Boolean>> requirements, Holder<PitchProvider> provider) implements PitchProvider {
	public static final MapCodec<StructureRequirement> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.list(
			Codec.mapPair(Identifier.CODEC.fieldOf("id"), Codec.BOOL.fieldOf("inside_piece")).codec()
		).fieldOf("requirements").forGetter(StructureRequirement::requirements),
		HOLDER_CODEC.fieldOf("provider").forGetter(StructureRequirement::provider)
	).apply(instance, StructureRequirement::new));

	@Override
	public float sample(Context context) {
		return this.provider.value().sample(context);
	}

	@Override
	public boolean applicable(Context context) {
		if (context.structureStatus().isEmpty()) return false;

		final StructureStatus status = context.structureStatus().get();
		return this.provider.value().applicable(context) &&
			this.requirements.stream().anyMatch(pair -> pair.getSecond() == status.insidePiece() && pair.getFirst().equals(status.structure()));
	}

	@Override
	public MapCodec<StructureRequirement> codec() {
		return MAP_CODEC;
	}

	@Override
	public void validate(ValidationContext context) {
		Validatable.validateHolder(context, "provider", this.provider);
	}
}
