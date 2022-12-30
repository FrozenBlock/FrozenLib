package net.frozenblock.lib.config.frozenlib_config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import net.minecraft.client.gui.screens.Screen;

public final class ClientScreenBuilder {

    public static ConfigScreenFactory<Screen> buildScreen() {
        return FrozenLibConfig::buildScreen;
    }

}
