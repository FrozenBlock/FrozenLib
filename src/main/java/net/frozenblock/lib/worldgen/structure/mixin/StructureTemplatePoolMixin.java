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

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.List;
import net.frozenblock.lib.worldgen.structure.impl.StructureTemplatePoolInterface;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(StructureTemplatePool.class)
public class StructureTemplatePoolMixin implements StructureTemplatePoolInterface {

	@Shadow
	@Final
	@Mutable
	private List<Pair<StructurePoolElement, Integer>> rawTemplates;

	@Shadow
	@Final
	private ObjectArrayList<StructurePoolElement> templates;

	@Unique
	@Override
	public synchronized void frozenlib$addTemplatePools(List<Pair<StructurePoolElement, Integer>> elements) {
		final List<Pair<StructurePoolElement, Integer>> finalRawTemplates = new ArrayList<>(this.rawTemplates);
		finalRawTemplates.addAll(elements);
		this.rawTemplates = finalRawTemplates;

		for (Pair<StructurePoolElement, Integer> pair : elements) {
			final StructurePoolElement structurePoolElement = pair.getFirst();
			for (int i = 0; i < pair.getSecond(); i++) this.templates.add(structurePoolElement);
		}
	}
}
