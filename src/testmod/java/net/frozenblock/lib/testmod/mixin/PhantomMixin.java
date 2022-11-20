package net.frozenblock.lib.testmod.mixin;

import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.spotting_icons.impl.EntitySpottingIconInterface;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Phantom.class)
public class PhantomMixin {

	@Inject(method = "<init>", at = @At("TAIL"))
	private void initWithIcon(EntityType<? extends Phantom> entityType, Level level, CallbackInfo ci) {
		Phantom phantom = Phantom.class.cast(this);
		((EntitySpottingIconInterface) phantom).getSpottingIconManager().setIcon(FrozenMain.id("textures/spotting_icons/phantom.png"), 16, 20, FrozenMain.id("default"));
	}
}
