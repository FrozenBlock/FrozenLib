/*
 * Copyright 2022 FrozenBlock
 * This file is part of FrozenLib.
 *
 * FrozenLib is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * FrozenLib is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with FrozenLib. If not, see <https://www.gnu.org/licenses/>.
 */

package org.quiltmc.qsl.frozenblock.worldgen.surface_rule.mixin;

import net.minecraft.world.level.levelgen.SurfaceRules;
import org.quiltmc.qsl.frozenblock.worldgen.surface_rule.impl.QuiltSequenceMaterialRuleHooks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

/**
 * Modified to work on Fabric
 */
@Mixin(SurfaceRules.SequenceRuleSource.class)
public class SequenceMaterialRuleMixin implements QuiltSequenceMaterialRuleHooks {
    @Mutable
    @Shadow
    @Final
    private List<SurfaceRules.RuleSource> sequence;

    @Override
    public void frozenblock_quilt$freeze() {
        this.sequence = List.copyOf(this.sequence);
    }
}
