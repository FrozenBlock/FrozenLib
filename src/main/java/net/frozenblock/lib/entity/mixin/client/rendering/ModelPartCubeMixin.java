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

package net.frozenblock.lib.entity.mixin.client.rendering;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import java.util.ArrayList;
import java.util.List;
import net.frozenblock.lib.entity.impl.client.rendering.CubeInvertInterface;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ModelPart.Cube.class)
public class ModelPartCubeMixin implements CubeInvertInterface {

	@Unique
	private boolean frozenLib$inverted = false;

	@ModifyExpressionValue(
		method = "compile",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/client/model/geom/ModelPart$Polygon;vertices:[Lnet/minecraft/client/model/geom/ModelPart$Vertex;"
		)
	)
	public ModelPart.Vertex[] frozenLib$invertModelPart(ModelPart.Vertex[] original) {
		if (this.frozenLib$inverted) {
			List<ModelPart.Vertex> vertices = new ArrayList<>();
			for (int i = 0; i < original.length; ++i) {
				vertices.add(original[(original.length - 1) - i]);
			}
			return vertices.toArray(new ModelPart.Vertex[0]);
		}
		return original;
	}

	@Override
	public void frozenLib$setInverted(boolean inverted) {
		this.frozenLib$inverted = inverted;
	}
}
