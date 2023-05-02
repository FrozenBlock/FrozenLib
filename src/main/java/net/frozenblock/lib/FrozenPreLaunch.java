package net.frozenblock.lib;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class FrozenPreLaunch implements PreLaunchEntrypoint {

    @Override
    public void onPreLaunch() {
        MixinExtrasBootstrap.init();
    }
}