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

package net.frozenblock.lib.worldgen.biome.mixin;

import java.util.function.Function;
import net.frozenblock.lib.worldgen.biome.impl.OverworldBiomeData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(targets = "net/minecraft/world/level/biome/MultiNoiseBiomeSourceParameterList$Preset$2", priority = 991)
public class OverworldBiomePresetMixin {

	@Inject(method = "apply", at = @At("RETURN"), cancellable = true)
	public <T> void apply(Function<ResourceKey<Biome>, T> function, CallbackInfoReturnable<Climate.ParameterList<T>> cir) {
		cir.setReturnValue(OverworldBiomeData.withModdedBiomeEntries(cir.getReturnValue(), function));
	}
}
