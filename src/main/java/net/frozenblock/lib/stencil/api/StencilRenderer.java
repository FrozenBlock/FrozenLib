package net.frozenblock.lib.stencil.api;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.entity.api.rendering.FrozenRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.apache.commons.compress.utils.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
				vertexConsumer.vertex(matrix4f, triangle.p0.x(), triangle.p0.y(), triangle.p0.z()).color(j, k, l, m).endVertex();
				vertexConsumer.vertex(matrix4f, triangle.p2.x(), triangle.p2.y(), triangle.p2.z()).color(j, k, l, m).endVertex();
				vertexConsumer.vertex(matrix4f, triangle.p1.x(), triangle.p1.y(), triangle.p1.z()).color(j, k, l, m).endVertex();
			}
		}
	}

	private static List<Triangle> makeOctahedron() {
		float f = (float)(1.0 / Math.sqrt(2.0));
		Vector3f third = new Vector3f(-1.0f, 0.0f, -1.0f);
		third.mul(f);
		Vector3f fourth = new Vector3f(1.0f, 0.0f, -1.0f);
		fourth.mul(f);
		Vector3f fifth = new Vector3f(1.0f, 0.0f, 1.0f);
		fourth.mul(f);
		Vector3f sixth = new Vector3f(-1.0f, 0.0f, 1.0f);
		sixth.mul(f);
		Vector3f[] vector3fs = new Vector3f[]{
				new Vector3f(0.0f, 1.0f, 0.0f),
				new Vector3f(0.0f, -1.0f, 0.0f),
				third,
				fourth,
				fifth,
				sixth
		};
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

	@Environment(EnvType.CLIENT)
	public record Triangle(Vector3f p0, Vector3f p1, Vector3f p2) {
		Collection<Triangle> subdivideSpherical() {
			Vector3f vector3f = normalize(div(add(this.p0, this.p1), 2F));
			Vector3f vector3f2 = normalize(div(add(this.p1, this.p2), 2F));
			Vector3f vector3f3 = normalize(div(add(this.p2, this.p0), 2F));
			return List.of(new Triangle(this.p0, vector3f, vector3f3), new Triangle(vector3f, this.p1, vector3f2), new Triangle(vector3f2, this.p2, vector3f3), new Triangle(vector3f, vector3f2, vector3f3));
		}
	}

	private static Vector3f add(Vector3f original, Vector3f add) {
		float x = original.x() + add.x();
		float y = original.y() + add.y();
		float z = original.z() + add.z();
		return new Vector3f(x, y, z);
	}

	private static Vector3f div(Vector3f vector3f, float scalar) {
		float inv = 1.0f / scalar;
		float x = vector3f.x() * inv;
		float y = vector3f.y() * inv;
		float z = vector3f.z() * inv;
		return new Vector3f(x, y, z);
	}

	private static Vector3f normalize(Vector3f vector3f) {
		float scalar = invsqrt(Math.fma(vector3f.x(), vector3f.x(), Math.fma(vector3f.y(), vector3f.y(), vector3f.z() * vector3f.z())));
		float x = vector3f.x() * scalar;
		float y = vector3f.y() * scalar;
		float z = vector3f.z() * scalar;
		return new Vector3f(x, y, z);
	}

	private static float invsqrt(float r) {
		return 1.0f / (float) java.lang.Math.sqrt(r);
	}
}
