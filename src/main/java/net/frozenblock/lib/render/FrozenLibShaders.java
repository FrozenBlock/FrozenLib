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
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.FrozenLibConstants;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.ShaderDefines;
import net.minecraft.client.renderer.ShaderProgram;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class FrozenLibShaders {
	private static final ShaderProgram RENDERTYPE_APPARITION = register("rendertype_apparition", DefaultVertexFormat.NEW_ENTITY);
	public static final RenderStateShard.ShaderStateShard RENDERTYPE_APPARITION_SHADER = new RenderStateShard.ShaderStateShard(RENDERTYPE_APPARITION);

	public static void init() {
	}

	private static @NotNull ShaderProgram register(String string, VertexFormat vertexFormat) {
		return register(FrozenLibConstants.MOD_ID, string, vertexFormat);
	}

	public static @NotNull ShaderProgram register(String modId, String string, VertexFormat vertexFormat) {
		return register(modId, string, vertexFormat, ShaderDefines.EMPTY);
	}

	public static @NotNull ShaderProgram register(String modId, String string, VertexFormat vertexFormat, ShaderDefines shaderDefines) {
		ShaderProgram shaderProgram = new ShaderProgram(ResourceLocation.fromNamespaceAndPath(modId, "core/" + string), vertexFormat, shaderDefines);
		CoreShaders.getProgramsToPreload().add(shaderProgram);
		return shaderProgram;
	}
}
