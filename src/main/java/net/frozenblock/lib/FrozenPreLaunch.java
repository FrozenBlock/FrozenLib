package net.frozenblock.lib;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class FrozenPreLaunch implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        /*if (FabricLoader.getInstance().isDevelopmentEnvironment() && FabricLoader.getInstance().isModLoaded("wilderwild")) {
            System.exit(69420);
        }*/
    }
}
