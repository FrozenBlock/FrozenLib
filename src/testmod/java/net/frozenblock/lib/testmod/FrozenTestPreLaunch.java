package net.frozenblock.lib.testmod;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfigOverrides;

public class FrozenTestPreLaunch implements PreLaunchEntrypoint {

    @Override
    public void onPreLaunch() {
        FrozenLibConfigOverrides.wardenSpawnTrackerCommand = true;
    }
}