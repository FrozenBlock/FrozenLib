package net.frozenblock.lib.block.mixin.waterlike;

import java.util.Optional;
import net.frozenblock.lib.block.api.waterlike.WaterLikeTypes;
import net.frozenblock.lib.block.impl.waterlike.WaterLikeType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.monster.Monster;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Monster.class)
public class MonsterMixin {

	@Inject(method = "getSwimSound", at = @At("HEAD"), cancellable = true)
	public void frozenLib$getSwimSound(CallbackInfoReturnable<SoundEvent> info) {
		final Optional<WaterLikeType> type = WaterLikeTypes.getRandomTouchingOrUnderWaterAndWaterLike(Monster.class.cast(this));
		if (type.isEmpty()) return;
		info.setReturnValue(type.get().hostileSwimSound().value());
	}

	@Inject(method = "getSwimSplashSound", at = @At("HEAD"), cancellable = true)
	public void frozenLib$getSwimSplashSound(CallbackInfoReturnable<SoundEvent> info) {
		final Optional<WaterLikeType> type = WaterLikeTypes.getRandomTouchingOrUnderWaterAndWaterLike(Monster.class.cast(this));
		if (type.isEmpty()) return;
		info.setReturnValue(type.get().hostileSplashSound().value());
	}
}
