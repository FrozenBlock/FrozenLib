package net.frozenblock.lib.replacements_and_lists;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.HashMap;
import java.util.Map;

public class SpawnRestrictionReplacements {

    public static Map<EntityType<?>, SpawnPlacements.Type> spawnPlacementTypes = new HashMap<>();
    public static Map<EntityType<?>, SpawnPlacements.SpawnPredicate<?>> spawnPredicates = new HashMap<>();
    public static Map<EntityType<?>, Heightmap.Types> heightmapTypes = new HashMap<>();

}
