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

import net.frozenblock.lib.worldgen.biome.impl.OverworldBiomeData;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MinecraftServer.class, priority = 990)
public class MinecraftServerMixin {

	@Shadow
	@Final
	protected WorldData worldData;

	@Shadow
	@Final
	private RegistryAccess.Frozen registryHolder;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void addOverworldBiomes(CallbackInfo ci) {
		this.worldData.worldGenSettings().dimensions().stream().forEach(dimensionOptions -> OverworldBiomeData.modifyBiomeSource(this.registryHolder.registryOrThrow(Registry.BIOME_REGISTRY), dimensionOptions.generator().getBiomeSource()));
	}
}
