/*
 * Copyright 2023-2024 FrozenBlock
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

package net.frozenblock.lib.stencil.api;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.entity.api.rendering.FrozenRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.apache.commons.compress.utils.Lists;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class StencilRenderer {
	private static final RenderType[] DYNAMIC_LIGHT = new RenderType[]{FrozenRenderType.dynamicLightStencil(), FrozenRenderType.dynamicLightColor()};

	public static final StencilRenderer.Triangle[] FACES_SPHERE = StencilRenderer.createNSphere(2);

	public static final StencilRenderer.Triangle[] FACES_CONE = StencilRenderer.createNCone(12);

	public static void render(Triangle[] triangles, Matrix4f matrix4f, MultiBufferSource multiBufferSource, int i) {
		int j = FastColor.ARGB32.red(i);
		int k = FastColor.ARGB32.green(i);
		int l = FastColor.ARGB32.blue(i);
		int m = FastColor.ARGB32.alpha(i);
		for (RenderType renderType : DYNAMIC_LIGHT) {
			VertexConsumer vertexConsumer = multiBufferSource.getBuffer(renderType);
			for (Triangle triangle : triangles) {
				vertexConsumer.vertex(matrix4f, triangle.p0.x, triangle.p0.y, triangle.p0.z).color(j, k, l, m).endVertex();
				vertexConsumer.vertex(matrix4f, triangle.p2.x, triangle.p2.y, triangle.p2.z).color(j, k, l, m).endVertex();
				vertexConsumer.vertex(matrix4f, triangle.p1.x, triangle.p1.y, triangle.p1.z).color(j, k, l, m).endVertex();
			}
		}
	}

	private static List<Triangle> makeOctahedron() {
		float f = (float)(1.0 / Math.sqrt(2.0));
		Vector3f[] vector3fs = new Vector3f[]{new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(0.0f, -1.0f, 0.0f), new Vector3f(-1.0f, 0.0f, -1.0f).mul(f), new Vector3f(1.0f, 0.0f, -1.0f).mul(f), new Vector3f(1.0f, 0.0f, 1.0f).mul(f), new Vector3f(-1.0f, 0.0f, 1.0f).mul(f)};
		ArrayList<Triangle> list = Lists.newArrayList();
		list.add(new Triangle(vector3fs[0], vector3fs[3], vector3fs[4]));
		list.add(new Triangle(vector3fs[0], vector3fs[4], vector3fs[5]));
		list.add(new Triangle(vector3fs[0], vector3fs[5], vector3fs[2]));
		list.add(new Triangle(vector3fs[0], vector3fs[2], vector3fs[3]));
		list.add(new Triangle(vector3fs[1], vector3fs[4], vector3fs[3]));
		list.add(new Triangle(vector3fs[1], vector3fs[5], vector3fs[4]));
		list.add(new Triangle(vector3fs[1], vector3fs[2], vector3fs[5]));
		list.add(new Triangle(vector3fs[1], vector3fs[3], vector3fs[2]));
		return list;
	}

	public static Triangle[] createNSphere(int i) {
		List<Triangle> list = StencilRenderer.makeOctahedron();
		for (int j = 0; j < i; ++j) {
			int k = list.size();
			for (int l = 0; l < k; ++l) {
				list.addAll(list.remove(0).subdivideSpherical());
			}
		}
		return list.toArray(new Triangle[0]);
	}

	public static Triangle[] createNCone(int i) {
		float f = (float)Math.PI * 2 / (float)i;
		ArrayList<Vector3f> list = Lists.newArrayList();
		for (int j = 0; j < i; ++j) {
			list.add(new Vector3f(Mth.cos((float)j * f), 0.0f, Mth.sin((float)j * f)));
		}
		Vector3f vector3f = new Vector3f(0.0f, 0.0f, 0.0f);
		Vector3f vector3f2 = new Vector3f(0.0f, -1.0f, 0.0f);
		ArrayList<Triangle> list2 = Lists.newArrayList();
		for (int k = 0; k < i; ++k) {
			list2.add(new Triangle(list.get(k), list.get((k + 1) % i), vector3f));
			list2.add(new Triangle(list.get((k + 1) % i), list.get(k), vector3f2));
		}
		return list2.toArray(new Triangle[0]);
	}

	@Environment(value=EnvType.CLIENT)
	public record Triangle(Vector3f p0, Vector3f p1, Vector3f p2) {
		Collection<Triangle> subdivideSpherical() {
			Vector3f vector3f = this.p0.add(this.p1, new Vector3f()).div(2.0f).normalize();
			Vector3f vector3f2 = this.p1.add(this.p2, new Vector3f()).div(2.0f).normalize();
			Vector3f vector3f3 = this.p2.add(this.p0, new Vector3f()).div(2.0f).normalize();
			return List.of(new Triangle(this.p0, vector3f, vector3f3), new Triangle(vector3f, this.p1, vector3f2), new Triangle(vector3f2, this.p2, vector3f3), new Triangle(vector3f, vector3f2, vector3f3));
		}
	}
}
