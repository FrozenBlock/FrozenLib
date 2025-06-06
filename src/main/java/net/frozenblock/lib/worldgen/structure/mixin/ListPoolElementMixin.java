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
import net.frozenblock.lib.worldgen.structure.impl.StructurePoolElementInterface;
import net.minecraft.world.level.levelgen.structure.pools.ListPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ListPoolElement.class)
public class ListPoolElementMixin implements StructurePoolElementInterface {

	@Shadow
	@Final
	private List<StructurePoolElement> elements;

	@Override
	public synchronized void frozenLib$addProcessors(List<StructureProcessor> processors) {
		this.elements.forEach(element -> {
			if (element instanceof StructurePoolElementInterface structurePoolElementInterface) {
				structurePoolElementInterface.frozenLib$addProcessors(processors);
			}
		});
	}
}
