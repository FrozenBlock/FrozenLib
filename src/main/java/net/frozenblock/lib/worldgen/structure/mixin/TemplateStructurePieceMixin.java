/*
 * Copyright (C) 2024-2025 FrozenBlock
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

package net.frozenblock.lib.worldgen.structure.mixin;

import java.util.List;
import net.frozenblock.lib.worldgen.structure.impl.InitialPieceProcessorInjectionInterface;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TemplateStructurePiece.class)
public abstract class TemplateStructurePieceMixin implements InitialPieceProcessorInjectionInterface {

	@Shadow
	protected StructurePlaceSettings placeSettings;

	@Override
	public void frozenLib$addProcessors(List<StructureProcessor> processors) {
		processors.forEach(structureProcessor ->  this.placeSettings.addProcessor(structureProcessor));
	}
}
