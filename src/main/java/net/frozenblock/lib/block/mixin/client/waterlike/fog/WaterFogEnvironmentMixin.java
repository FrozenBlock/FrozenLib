/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.block.mixin.client.waterlike.fog;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.block.client.impl.waterlike.WaterLikeFogUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.fog.environment.WaterFogEnvironment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeProbe;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(WaterFogEnvironment.class)
public class WaterFogEnvironmentMixin {

	@Unique
	private static boolean frozenLib$previouslyApplicable = false;

	@WrapOperation(
		method = "setupFog",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/attribute/EnvironmentAttributeProbe;getValue(Lnet/minecraft/world/attribute/EnvironmentAttribute;F)Ljava/lang/Object;"
		)
	)
	public Object frozenLib$modifyFogDistance(EnvironmentAttributeProbe instance, EnvironmentAttribute<Object> attribute, float partialTicks, Operation<Object> original) {
		final Object object = original.call(instance, attribute, partialTicks);
		if (!(object instanceof Float flt)) return object;
		return WaterLikeFogUtil.getModifiedFogDistance(partialTicks, flt);
	}

	@ModifyReturnValue(method = "isApplicable", at = @At("RETURN"))
	public boolean frozenLib$clearWaterLikeFogIfNotApplicable(
		boolean original,
		@Local(argsOnly = true) FogType fogType,
		@Local(argsOnly = true) Entity entity
	) {
		if (!original) {
			WaterLikeFogUtil.reset(true);
		} else if (!frozenLib$previouslyApplicable && entity != null) {
			final BlockPos pos = Minecraft.getInstance().gameRenderer.mainCamera().blockPosition();
			WaterLikeFogUtil.tick(entity.level(), pos, fogType, true);
		}
		frozenLib$previouslyApplicable = original;
		return original;
	}

	@ModifyReturnValue(method = "getBaseColor", at = @At("RETURN"))
	public int frozenLib$modifyFogColor(
		int original,
		@Local(argsOnly = true) float partialTicks
	) {
		return WaterLikeFogUtil.getModifiedFogColor(partialTicks, original);
	}

}
