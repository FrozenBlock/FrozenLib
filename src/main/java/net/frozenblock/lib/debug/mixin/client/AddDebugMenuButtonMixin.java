package net.frozenblock.lib.debug.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.frozenblock.lib.debug.client.impl.DebugScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(PauseScreen.class)
public abstract class AddDebugMenuButtonMixin extends Screen {

	protected AddDebugMenuButtonMixin(Component title) {
		super(title);
	}

	@Inject(
		method = "createPauseMenu",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/layouts/GridLayout$RowHelper;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;)Lnet/minecraft/client/gui/layouts/LayoutElement;",
			ordinal = 0,
			shift = At.Shift.BEFORE
		)
	)
	private void frozenLib$addDebugOption(
		CallbackInfo info,
		@Local GridLayout.RowHelper rowHelper
	) {
		if (FrozenLibConfig.IS_DEBUG) {
			rowHelper.addChild(Button.builder(DebugScreen.DEBUG, button -> {
				this.minecraft.setScreen(new DebugScreen());
			}).width(204).build(), 2);
		}
	}

}
