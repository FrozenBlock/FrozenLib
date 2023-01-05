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

package net.frozenblock.lib.worldgen.surface.mixin;

import net.frozenblock.lib.worldgen.surface.impl.SetNoiseGeneratorPresetInterface;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BootstapContext.class)
public interface BootstapContextMixin<T> {

	@Inject(method = {"register(Lnet/minecraft/resources/ResourceKey;Ljava/lang/Object;)Lnet/minecraft/core/Holder$Reference;"}, at = @At("HEAD"))
	default void register(ResourceKey<T> registryKey, T value, CallbackInfoReturnable<net.minecraft.core.Holder.Reference<T>> info) {
		if (value instanceof NoiseGeneratorSettings noiseGeneratorSettings) {
			SetNoiseGeneratorPresetInterface.class.cast(noiseGeneratorSettings).setPreset(registryKey.location());
		}
	}

}
