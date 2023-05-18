/*
 * Copyright 2023 QuiltMC
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
    public void frozenLib_quilt$freeze() {
        this.sequence = List.copyOf(this.sequence);
    }
}
