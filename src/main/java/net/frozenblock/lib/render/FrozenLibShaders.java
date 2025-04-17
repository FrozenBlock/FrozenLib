/*
 * Copyright (C) 2024-2025 FrozenBlock
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

package net.frozenblock.lib.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.render.api.ShaderRegistryAPI;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.ShaderInstance;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class FrozenLibShaders {
	public static final RenderStateShard.ShaderStateShard RENDERTYPE_APPARITION_SHADER = new RenderStateShard.ShaderStateShard(FrozenLibShaders::getApparitionShader);
	@Nullable
	private static ShaderInstance apparitionShader;

	@Nullable
	public static ShaderInstance getApparitionShader() {
		return apparitionShader;
	}

	public static void init() {
		ShaderRegistryAPI.SHADERS_LOADING.register((resourceProvider, shaderList) -> {
			ShaderRegistryAPI.registerShader(
				shaderList,
				new ShaderInstance(resourceProvider, FrozenLibConstants.safeString("rendertype_apparition"), DefaultVertexFormat.NEW_ENTITY),
				shaderInstance -> apparitionShader = shaderInstance
			);
		});
	}
}
