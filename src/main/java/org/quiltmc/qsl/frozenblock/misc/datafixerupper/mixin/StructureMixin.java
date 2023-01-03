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

package org.quiltmc.qsl.frozenblock.misc.datafixerupper.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.impl.QuiltDataFixesInternals;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Modified to work on Fabric
 */
@Mixin(value = StructureTemplate.class, priority = 1001)
public abstract class StructureMixin {
    @Inject(method = "save", at = @At("TAIL"), cancellable = true)
    private void addModDataVersions(CompoundTag compound, CallbackInfoReturnable<CompoundTag> cir) {
        CompoundTag out = cir.getReturnValue();
        QuiltDataFixesInternals.get().addModDataVersions(out);
        cir.setReturnValue(out);
    }
}
