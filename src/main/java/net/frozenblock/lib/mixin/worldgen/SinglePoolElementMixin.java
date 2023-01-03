/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.mixin.worldgen;

import com.mojang.datafixers.util.Either;
import net.frozenblock.lib.impl.StructurePoolElementIdReplacements;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
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

    @Inject(method = "<init>(Lcom/mojang/datafixers/util/Either;Lnet/minecraft/core/Holder;Lnet/minecraft/world/level/levelgen/structure/pools/StructureTemplatePool$Projection;)V", at = @At("TAIL"))
    public void replaceStructure(Either<ResourceLocation, StructureTemplate> template, Holder<StructureProcessorList> processors, StructureTemplatePool.Projection projection, CallbackInfo info) {
        if (template.left().isPresent()) {
            ResourceLocation id = template.left().get();
            if (StructurePoolElementIdReplacements.RESOURCE_LOCATION_REPLACEMENTS.containsKey(id)) {
                this.template = Either.left(StructurePoolElementIdReplacements.RESOURCE_LOCATION_REPLACEMENTS.get(id));
            }
        }
    }

}
