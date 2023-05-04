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

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.quiltmc.qsl.frozenblock.worldgen.surface_rule.api.SurfaceRuleEvents;
import org.quiltmc.qsl.frozenblock.worldgen.surface_rule.impl.SurfaceRuleContextImpl;
import org.quiltmc.qsl.frozenblock.worldgen.surface_rule.impl.VanillaSurfaceRuleTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This modifies the Vanilla surface rules using the {@link SurfaceRuleEvents}.
 * <p>
 * Modified to work on Fabric
 */
@Mixin(value = SurfaceRuleData.class, priority = 1005)
public class VanillaSurfaceRulesMixin {

    @ModifyReturnValue(
        method = "overworldLike",
        at = @At("RETURN")
    )
    private static SurfaceRules.RuleSource frozenblock_quilt$injectOverworldLikeRules(SurfaceRules.RuleSource source, boolean abovePreliminarySurface, boolean bedrockRoof, boolean bedrockFloor) {
        if (!VanillaSurfaceRuleTracker.OVERWORLD.isPaused()) {
            return VanillaSurfaceRuleTracker.OVERWORLD.modifyMaterialRules(new SurfaceRuleContextImpl.OverworldImpl(
                abovePreliminarySurface, bedrockRoof, bedrockFloor, source
            ));
        }
        return source;
    }

    @ModifyReturnValue(
            method = "nether",
            at = @At("RETURN")
    )
    private static SurfaceRules.RuleSource frozenblock_quilt$injectNetherRules(SurfaceRules.RuleSource source) {
        if (!VanillaSurfaceRuleTracker.NETHER.isPaused()) {
            return VanillaSurfaceRuleTracker.NETHER.modifyMaterialRules(new SurfaceRuleContextImpl.NetherImpl(
                    source
            ));
        }
		return source;
	}

    @ModifyReturnValue(
        method = "end",
        at = @At("RETURN")
    )
    private static SurfaceRules.RuleSource frozenblock_quilt$injectEndRules(SurfaceRules.RuleSource source) {
        if (!VanillaSurfaceRuleTracker.THE_END.isPaused()) {
            return VanillaSurfaceRuleTracker.THE_END.modifyMaterialRules(new SurfaceRuleContextImpl.TheEndImpl(
                source
            ));
        }
        return source;
    }
}
