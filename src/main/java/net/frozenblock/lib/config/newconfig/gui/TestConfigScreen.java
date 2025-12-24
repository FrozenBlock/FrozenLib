package net.frozenblock.lib.config.newconfig.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public class TestConfigScreen extends Screen {

	protected TestConfigScreen(Component title) {
		super(title);
	}

}
