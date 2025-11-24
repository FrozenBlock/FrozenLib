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

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import java.util.ArrayList;
import java.util.List;
import net.frozenblock.lib.worldgen.structure.impl.StructurePoolElementInterface;
import net.frozenblock.lib.worldgen.structure.impl.StructureTemplateInterface;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SinglePoolElement.class)
public class SinglePoolElementMixin implements StructurePoolElementInterface {

	@Unique
	private final List<StructureProcessor> frozenLib$additionalProcessors = new ArrayList<>();

	@Override
	public synchronized void frozenLib$addProcessors(List<StructureProcessor> processors) {
		this.frozenLib$additionalProcessors.addAll(processors);
	}

	@ModifyExpressionValue(
		method = "place",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/levelgen/structure/pools/SinglePoolElement;getTemplate(Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;)Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;"
		)
	)
	public StructureTemplate frozenLib$place(StructureTemplate original) {
		if (original instanceof StructureTemplateInterface structureTemplateInterface) structureTemplateInterface.frozenLib$addProcessors(this.frozenLib$additionalProcessors);
		return original;
	}

}
