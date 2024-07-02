/*
 * Copyright (C) 2024 FrozenBlock
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
import net.frozenblock.lib.worldgen.structure.impl.StructureTemplateInterface;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import java.util.Optional;

@Mixin(StructureTemplateManager.class)
public class StructureTemplateManagerMixin {

	@ModifyExpressionValue(method = "getOrCreate", at = @At("RETURN"))
	public StructureTemplate frozenLib$appendIdToTemplate(StructureTemplate template, ResourceLocation id) {
		if (template instanceof StructureTemplateInterface structureTemplateInterface) {
			structureTemplateInterface.frozenLib$setId(id);
		}

		return template;
	}

	@ModifyExpressionValue(method = "get", at = @At("RETURN"))
	public Optional<StructureTemplate> frozenLib$appendIdToTemplate(Optional<StructureTemplate> template, ResourceLocation id) {
		if (template.isPresent() && template.get() instanceof StructureTemplateInterface structureTemplateInterface) {
			structureTemplateInterface.frozenLib$setId(id);
		}

		return template;
	}

}
