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

package org.quiltmc.qsl.frozenblock.worldgen.surface_rule.mixin;

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

    @ModifyVariable(
            method = "overworldLike",
            at = @At("STORE"),
			ordinal = 8
    )
    private static SurfaceRules.RuleSource frozenblock_quilt$injectOverworldLikeRules(SurfaceRules.RuleSource source, boolean abovePreliminarySurface, boolean bedrockRoof, boolean bedrockFloor) {
        if (!VanillaSurfaceRuleTracker.OVERWORLD.isPaused()) {
            return VanillaSurfaceRuleTracker.OVERWORLD.modifyMaterialRules(new SurfaceRuleContextImpl.OverworldImpl(
                    abovePreliminarySurface, bedrockRoof, bedrockFloor, source
            ));
        }
		return source;
    }

    @ModifyVariable(
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
