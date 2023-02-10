package net.frozenblock.lib.config.api.client;

import net.minecraft.client.gui.screens.Screen;

public class FrozenConfigScreen extends Screen {
	public FrozenConfigScreen(FrozenConfig config, Screen parent) {
		super(config.title());
	}
}
