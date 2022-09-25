package net.frozenblock.lib;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class FrozenPreLaunch implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        /*if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            System.exit(69420);
        }*/
    }
}
