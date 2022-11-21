package net.frozenblock.lib.wind;

import net.frozenblock.lib.math.EasyNoiseSampler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public class WindManager {
	public static long time;
	public static double windX;
	public static double windY;
	public static double windZ;
	public static double cloudX;
	public static double cloudY;
	public static double cloudZ;

	public static void tick(ServerLevel level) {
		float thunderLevel = level.getThunderLevel(1F) * 0.03F;
		double calcTime = time * 0.0005;
		double calcTimeY = time * 0.00035;
		Vec3 vec3 = EasyNoiseSampler.sampleVec3(EasyNoiseSampler.perlinXoro, calcTime, calcTimeY, calcTime);
		windX = vec3.x + (vec3.x * thunderLevel);
		windY = vec3.y + (vec3.y * thunderLevel);
		windZ = vec3.z + (vec3.z * thunderLevel);
		cloudX += (windX * 0.025);
		cloudY += (windY * 0.005);
		cloudZ += (windZ * 0.025);
	}
}
