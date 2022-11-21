package net.frozenblock.lib.wind;

import net.frozenblock.lib.math.EasyNoiseSampler;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class ClientWindManager {
	public static long time;
	public static double prevWindX;
	public static double prevWindY;
	public static double prevWindZ;
	public static double windX;
	public static double windY;
	public static double windZ;

	public static double targetCloudY;
	public static double prevCloudX;
	public static double prevCloudY;
	public static double prevCloudZ;
	public static double cloudX;
	public static double cloudY;
	public static double cloudZ;

	public static boolean hasSynced;

	public static void tick() {
		prevWindX = windX;
		prevWindY = windY;
		prevWindZ = windZ;
		time += 1;
		long calcTime = time / 200;
		long calcTimeY = time / 400;
		Vec3 vec3 = EasyNoiseSampler.sampleVec3(EasyNoiseSampler.perlinXoro, calcTime, calcTimeY, calcTimeY);
		windX = vec3.x;
		windY = vec3.y;
		windZ = vec3.z;
		//CLOUDS
		targetCloudY = vec3.y();
		prevCloudX = cloudX;
		prevCloudY = cloudY;
		prevCloudZ = cloudZ;
		cloudX += windX * 0.05;
		if (cloudY > targetCloudY) {
			cloudY -= windY * 0.005;
		} else if (cloudY < targetCloudY) {
			cloudY += windY * 0.005;
		}
		cloudZ += windZ * 0.05;
	}

	public static double getWindX(float partialTick) {
		return Mth.lerp(partialTick, prevWindX, windX);
	}

	public static double getWindY(float partialTick) {
		return Mth.lerp(partialTick, prevWindY, windY);
	}

	public static double getWindZ(float partialTick) {
		return Mth.lerp(partialTick, prevWindZ, windZ);
	}

	public static double getCloudX(float partialTick) {
		return Mth.lerp(partialTick, prevCloudX, cloudX);
	}

	public static double getCloudY(float partialTick) {
		return Mth.lerp(partialTick, prevCloudY, cloudY);
	}

	public static double getCloudZ(float partialTick) {
		return Mth.lerp(partialTick, prevCloudZ, cloudZ);
	}

}
