package net.frozenblock.lib;

import net.fabricmc.loader.api.FabricLoader;

public class FrozenBools {

    public static boolean useNewDripstoneLiquid = false;

    // EXTERNAL MODS
	public static final boolean hasArchitectury = FabricLoader.getInstance().isModLoaded("architectury");
	public static final boolean hasBCLib = FabricLoader.getInstance().isModLoaded("bclib");
    public static final boolean hasCloth = FabricLoader.getInstance().isModLoaded("cloth-config");
	public static final boolean hasContinuity = FabricLoader.getInstance().isModLoaded("continuity");
	public static final boolean hasEntityCulling = FabricLoader.getInstance().isModLoaded("entityculling");
	public static final boolean hasGeckoLib = FabricLoader.getInstance().isModLoaded("geckolib3");
	public static final boolean hasIndium = FabricLoader.getInstance().isModLoaded("indium");
	public static final boolean hasIris = FabricLoader.getInstance().isModLoaded("iris");
    public static final boolean hasPipes = FabricLoader.getInstance().isModLoaded("copper_pipe");
	public static final boolean hasRoughlyEnoughItems = FabricLoader.getInstance().isModLoaded("roughlyenoughitems");
	public static final boolean hasRoughlyEnoughResources = FabricLoader.getInstance().isModLoaded("roughlyenoughresources");
	public static final boolean hasRepurposedStructures = FabricLoader.getInstance().isModLoaded("repurposed_structures");
	public static final boolean hasServerCore = FabricLoader.getInstance().isModLoaded("servercore");
    public static final boolean hasSodium = FabricLoader.getInstance().isModLoaded("sodium");
	public static final boolean hasStarlight = FabricLoader.getInstance().isModLoaded("starlight");
    public static final boolean hasTerraBlender = FabricLoader.getInstance().isModLoaded("terrablender");
    public static final boolean hasTerralith = FabricLoader.getInstance().isModLoaded("terralith");
}
