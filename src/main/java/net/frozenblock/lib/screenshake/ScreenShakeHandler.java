package net.frozenblock.lib.screenshake;

import java.util.ArrayList;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;

public class ScreenShakeHandler {

	private static final ArrayList<ScreenShake> screenShakes = new ArrayList<>();
	private static final ArrayList<ScreenShake> shakesToRemove = new ArrayList<>();

	public static void tick(ClientLevel level, Vec3 playerPos) {
		float highestIntensity = 0F;
		float totalIntensity = 0F;
		int amount = 0;
		for (ScreenShake shake : screenShakes) {
			amount += 1;
			float shakeIntensity = shake.getIntensity(playerPos);
			totalIntensity += shakeIntensity;
			highestIntensity = Math.max(shakeIntensity, highestIntensity);
			shake.duration -= 1;
			if (shake.duration <= 0) {
				shakesToRemove.add(shake);
			}
		}
		if (amount > 0 && totalIntensity != 0 && highestIntensity != 0) {
			float intensity = (highestIntensity + (totalIntensity / amount)) * 0.5F;
			
		}
		screenShakes.removeAll(shakesToRemove);
		shakesToRemove.clear();
	}

	public static void addShake(float intensity, int duration, Vec3 pos, float maxDistance) {
		screenShakes.add(new ScreenShake(intensity, duration, pos, maxDistance));
	}

	public static class ScreenShake {

		private final float intensity;
		public int duration;
		public final Vec3 pos;
		public final float maxDistance;

		public ScreenShake(float intensity, int duration, Vec3 pos, float maxDistance) {
			this.intensity = intensity;
			this.duration = duration;
			this.pos = pos;
			this.maxDistance = maxDistance;
		}

		public float getIntensity(Vec3 playerPos) {
			return (float) (1F - (playerPos.distanceTo(this.pos) / this.maxDistance) * -1F);
		}

	}

}
