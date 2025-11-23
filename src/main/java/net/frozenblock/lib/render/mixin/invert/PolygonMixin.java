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

package net.frozenblock.lib.render.mixin.invert;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.entity.impl.client.rendering.ModelPartInvertInterface;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Environment(EnvType.CLIENT)
@Mixin(ModelPart.Polygon.class)
public class PolygonMixin implements ModelPartInvertInterface {

	@Mutable
	@Shadow
	@Final
	ModelPart.Vertex[] vertices;

	@Override
	public void frozenLib$setInverted() {
		final List<ModelPart.Vertex> newVertices = new ArrayList<>();
		for (int i = 0; i < this.vertices.length; ++i) {
			newVertices.add(this.vertices[(this.vertices.length - 1) - i]);
		}
		this.vertices = newVertices.toArray(new ModelPart.Vertex[0]);
	}
}
