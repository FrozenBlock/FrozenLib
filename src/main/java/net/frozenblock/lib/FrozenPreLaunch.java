package net.frozenblock.lib;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class FrozenPreLaunch implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        /*if (FabricLoader.getInstance().isDevelopmentEnvironment()) if (FabricLoader.getInstance().getModContainer("wilderwild").isEmpty()) {
            System.exit(69420);
        }*/
    }
}
