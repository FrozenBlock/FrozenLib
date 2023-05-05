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
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MinecraftServer.class, priority = 991)
public class MinecraftServerMixin {

    @Shadow
	@Final
	private LayeredRegistryAccess<RegistryLayer> registries;

	@Shadow
	public abstract RegistryAccess.Frozen registryAccess();

	@Inject(method = "<init>", at = @At("RETURN"))
	private void addOverworldBiomes(CallbackInfo ci) {
		this.registries.compositeAccess().registryOrThrow(Registries.LEVEL_STEM).stream().forEach(dimensionOptions -> OverworldBiomeData.modifyBiomeSource(registryAccess().lookupOrThrow(Registries.BIOME), dimensionOptions.generator().getBiomeSource()));
	}
}
