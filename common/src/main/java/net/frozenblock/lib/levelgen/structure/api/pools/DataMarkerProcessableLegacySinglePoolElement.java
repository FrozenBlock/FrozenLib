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

package net.frozenblock.lib.levelgen.structure.api.pools;

import com.mojang.datafixers.util.Either;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public abstract class DataMarkerProcessableLegacySinglePoolElement extends DataMarkerProcessableSinglePoolElement {

	public DataMarkerProcessableLegacySinglePoolElement(
		Either<Identifier, StructureTemplate> either,
		Holder<StructureProcessorList> holder,
		StructureTemplatePool.Projection projection,
		Optional<LiquidSettings> optional
	) {
		super(either, holder, projection, optional);
	}

	@Override
	protected StructurePlaceSettings getSettings(Rotation rotation, BoundingBox boundingBox, LiquidSettings liquidSettings, boolean offset) {
		StructurePlaceSettings structurePlaceSettings = super.getSettings(rotation, boundingBox, liquidSettings, offset);
		structurePlaceSettings.popProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
		structurePlaceSettings.addProcessor(BlockIgnoreProcessor.AIR);
		return structurePlaceSettings;
	}

	@Override
	public String toString() {
		return "DataMarkerProcessableLegacySingle[" + this.template + "]";
	}
}
