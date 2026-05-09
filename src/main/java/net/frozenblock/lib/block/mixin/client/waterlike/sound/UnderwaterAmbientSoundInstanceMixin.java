package net.frozenblock.lib.block.mixin.client.waterlike.sound;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.block.api.waterlike.WaterLikeTypes;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.UnderwaterAmbientSoundInstances;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(UnderwaterAmbientSoundInstances.UnderwaterAmbientSoundInstance.class)
public class UnderwaterAmbientSoundInstanceMixin {

	@Shadow
	@Final
	private LocalPlayer player;

	@ModifyExpressionValue(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/player/LocalPlayer;isUnderWater()Z"
		)
	)
	public boolean frozenLib$stopAdditionsIfInWaterLike(boolean original) {
		if (WaterLikeTypes.getRandomPlayerInside(this.player).isPresent()) return false;
		return original;
	}

}
