/*
 * Copyright 2022 QuiltMC
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

import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.quiltmc.qsl.frozenblock.worldgen.surface_rule.api.SurfaceRuleEvents;
import org.quiltmc.qsl.frozenblock.worldgen.surface_rule.impl.SurfaceRuleContextImpl;
import org.quiltmc.qsl.frozenblock.worldgen.surface_rule.impl.VanillaSurfaceRuleTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This modifies the Vanilla surface rules using the {@link SurfaceRuleEvents}.
 * <p>
 * Modified to work on Fabric
 */
@Mixin(value = SurfaceRuleData.class, priority = 69420)
public abstract class VanillaSurfaceRulesMixin {
    @Inject(
            method = "overworldLike",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void frozenblock_quilt$injectOverworldRules(boolean abovePreliminarySurface, boolean bedrockRoof, boolean bedrockFloor,
                                                   CallbackInfoReturnable<SurfaceRules.RuleSource> cir) {
        if (!VanillaSurfaceRuleTracker.OVERWORLD.isPaused()) {
            cir.setReturnValue(VanillaSurfaceRuleTracker.OVERWORLD.modifyMaterialRules(new SurfaceRuleContextImpl.OverworldImpl(
                    abovePreliminarySurface, bedrockRoof, bedrockFloor, cir.getReturnValue()
            )));
        }
    }

    @Inject(
            method = "nether",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void frozenblock_quilt$injectNetherRules(CallbackInfoReturnable<SurfaceRules.RuleSource> cir) {
        if (!VanillaSurfaceRuleTracker.NETHER.isPaused()) {
            cir.setReturnValue(VanillaSurfaceRuleTracker.NETHER.modifyMaterialRules(new SurfaceRuleContextImpl.NetherImpl(
                    cir.getReturnValue()
            )));
        }
    }

    @Inject(
            method = "end",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void frozenblock_quilt$injectEndRules(CallbackInfoReturnable<SurfaceRules.RuleSource> cir) {
        if (!VanillaSurfaceRuleTracker.THE_END.isPaused()) {
            cir.setReturnValue(VanillaSurfaceRuleTracker.THE_END.modifyMaterialRules(new SurfaceRuleContextImpl.TheEndImpl(
                    cir.getReturnValue()
            )));
        }
    }
}
