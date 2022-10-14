package net.frozenblock.lib;

import net.fabricmc.loader.api.FabricLoader;

public class FrozenBools {

    public static boolean useNewDripstoneLiquid = false;

    // EXTERNAL MODS
    public static boolean hasCloth = FabricLoader.getInstance().isModLoaded("cloth-config");
    public static boolean hasPipes = FabricLoader.getInstance().isModLoaded("copper_pipe");
    public static boolean hasSodium = FabricLoader.getInstance().isModLoaded("sodium");
    public static boolean hasTerraBlender = FabricLoader.getInstance().isModLoaded("terrablender");
    public static boolean hasTerralith = FabricLoader.getInstance().isModLoaded("terralith");
}
