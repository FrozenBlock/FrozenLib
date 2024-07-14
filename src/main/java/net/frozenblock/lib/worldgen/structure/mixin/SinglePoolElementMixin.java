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

import com.mojang.datafixers.util.Either;
import java.util.Optional;
import net.frozenblock.lib.worldgen.structure.api.StructurePoolElementIdReplacements;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SinglePoolElement.class)
public class SinglePoolElementMixin {

    @Shadow
	@Final
	@Mutable
    protected Either<ResourceLocation, StructureTemplate> template;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	@Inject(method = "<init>", at = @At("TAIL"))
    public void replaceStructure(Either<ResourceLocation, StructureTemplate> template, Holder<StructureProcessorList> processors, StructureTemplatePool.Projection projection, Optional<LiquidSettings> overrideLiquidSettings, CallbackInfo info) {
        if (template.left().isPresent()) {
            ResourceLocation id = template.left().get();
            if (StructurePoolElementIdReplacements.RESOURCE_LOCATION_REPLACEMENTS.containsKey(id)) {
                this.template = Either.left(StructurePoolElementIdReplacements.RESOURCE_LOCATION_REPLACEMENTS.get(id));
            }
        }
    }

}
