package net.frozenblock.lib.wind;

import net.frozenblock.lib.math.EasyNoiseSampler;
import net.minecraft.world.phys.Vec3;

public class WindManager {
	public static long time;
	public static double windX;
	public static double windY;
	public static double windZ;

	public static void tick() {
		long calcTime = time / 200;
		long calcTimeY = time / 2000;
		Vec3 vec3 = EasyNoiseSampler.sampleVec3(EasyNoiseSampler.perlinXoro, calcTime, calcTimeY, calcTimeY);
		windX = vec3.x;
		windY = vec3.y;
		windZ = vec3.z;
	}
}
