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

package net.frozenblock.lib.levelgen.biome.api.modifications.data;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.levelgen.biome.api.modifications.BiomeModificationContext;
import net.frozenblock.lib.levelgen.biome.api.modifications.ModificationPhase;
import net.frozenblock.lib.levelgen.biome.api.modifications.data.selector.BiomeSelector;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.ValidationContext;

public class NoOpModification extends BiomeModification {
	protected static final NoOpModification INSTANCE = new NoOpModification();
	public static final MapCodec<NoOpModification> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.point(INSTANCE));

	private NoOpModification() {
		super(Holder.direct(BiomeSelector.alwaysFalse()), ModificationPhase.POST_PROCESSING);
	}

	protected static MapCodec<NoOpModification> createDummyCodec() {
		return RecordCodecBuilder.mapCodec(instance -> instance.point(new NoOpModification()));
	}

	@Override
	public void modify(BiomeModificationContext context) {}

	@Override
	BiomeModificationType<? extends BiomeModification> type() {
		return BiomeModificationTypes.NO_OP.get();
	}

	@Override
	public void validate(ValidationContext context) {}
}
