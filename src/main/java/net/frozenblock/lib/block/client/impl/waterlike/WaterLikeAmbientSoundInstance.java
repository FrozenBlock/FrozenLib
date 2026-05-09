package net.frozenblock.lib.block.client.impl.waterlike;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.block.impl.waterlike.WaterLikeType;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.UnderwaterAmbientSoundInstances;
import net.minecraft.sounds.SoundSource;

@Environment(EnvType.CLIENT)
public class WaterLikeAmbientSoundInstance extends AbstractTickableSoundInstance {
	public static final int FADE_DURATION = UnderwaterAmbientSoundInstances.UnderwaterAmbientSoundInstance.FADE_DURATION;
	private final WaterLikeType type;
	private final LocalPlayer player;
	private int fade;

	public WaterLikeAmbientSoundInstance(WaterLikeType type, LocalPlayer localPlayer) {
		super(type.ambientSound().value(), SoundSource.AMBIENT, SoundInstance.createUnseededRandom());
		this.type = type;
		this.player = localPlayer;
		this.looping = true;
		this.delay = 0;
		this.volume = 1F;
		this.relative = true;
	}

	@Override
	public void tick() {
		if (this.player.isRemoved() || this.fade < 0) {
			this.stop();
			return;
		}

		if (this.player.isUnderWater() && this.player.frozenLib$wasPlayerInWaterLike(this.type)) {
			this.fade++;
		} else {
			this.fade -= 2;
		}

		this.fade = Math.min(this.fade, FADE_DURATION);
		this.volume = Math.max(0F, Math.min((float) this.fade / FADE_DURATION, 1F));
	}
}
