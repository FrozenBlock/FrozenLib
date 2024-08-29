package net.frozenblock.lib.core.client.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public class FrustumUtil {

	public static boolean isVisible(AABB aabb) {
		Frustum frustum = Minecraft.getInstance().levelRenderer.cullingFrustum;
		if (frustum != null) {
			return frustum.isVisible(aabb);
		}
		return true;
	}

	public static boolean isVisible(Vec3 pos, double area) {
		return isVisible(AABB.ofSize(pos, area, area, area));
	}

}
