/*
 * Copyright (C) 2025-2026 FrozenBlock
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

package net.frozenblock.lib.resource.mixin.client.texture;

import net.frozenblock.lib.resource.client.api.texture.ServerTextureManager;
import net.frozenblock.lib.resource.client.impl.texture.MinecraftServerTextureInterface;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.renderer.texture.TextureManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@ClientOnly
@Mixin(Minecraft.class)
public class MinecraftMixin implements MinecraftServerTextureInterface {
	@Shadow
	@Final
	private TextureManager textureManager;
	@Unique
	private ServerTextureManager frozenLib$serverTextureManager;

	@Inject(method = "<init>", at = @At("RETURN"))
	public void frozenLib$initServerTextureManager(GameConfig gameConfig, CallbackInfo info) {
		this.frozenLib$serverTextureManager = new ServerTextureManager(this.textureManager, Minecraft.class.cast(this));
	}

	@Unique
	@Nullable
	@Override
	public ServerTextureManager frozenLib$serverTextureManager() {
		return this.frozenLib$serverTextureManager;
	}
}
