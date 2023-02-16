package net.frozenblock.lib.testmod.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screens.Screen;

public class ModMenuInit implements ModMenuApi {

	@Override
	public ConfigScreenFactory<Screen> getModConfigScreenFactory() {
		return TestConfig.get().config()::makeGui;
	}
}
