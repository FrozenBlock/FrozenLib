package net.frozenblock.lib.testmod.mixin;

import com.mojang.authlib.GameProfile;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.spotting_icons.impl.EntitySpottingIconInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {

	@Inject(method = "<init>", at = @At("TAIL"))
	private void initWithIcon(Level level, BlockPos pos, float yRot, GameProfile gameProfile, ProfilePublicKey profilePublicKey, CallbackInfo ci) {
		Player player = Player.class.cast(this);
		player.getSpottingIconManager().setIcon(FrozenMain.id("textures/spotting_icons/player.png"), 0, 1, FrozenMain.id("default"));
	}
}
