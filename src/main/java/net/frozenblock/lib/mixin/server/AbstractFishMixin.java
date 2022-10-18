package net.frozenblock.lib.mixin.server;

import net.frozenblock.lib.entities.NoFlopAbstractFish;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFish.class)
public final class AbstractFishMixin extends WaterAnimal {

	private AbstractFishMixin(EntityType<? extends WaterAnimal> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(method = "aiStep", at = @At("HEAD"), cancellable = true)
	private void noFlop(CallbackInfo ci) {
		AbstractFish fish = AbstractFish.class.cast(this);
		if (fish instanceof NoFlopAbstractFish) {
			ci.cancel();
			super.aiStep();
		}
	}
}
