package net.frozenblock.lib.mixin.server;

import net.frozenblock.lib.replacements_and_lists.SpawnRestrictionReplacements;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(SpawnPlacements.class)
public class SpawnRestrictionMixin {

    @Final
    @Shadow
    private static Map<EntityType<?>, SpawnPlacements.Data> DATA_BY_TYPE;

    @Inject(method = "register", at = @At("HEAD"), cancellable = true)
    private static <T extends Mob> void register(EntityType<T> type, SpawnPlacements.Type location, Heightmap.Types heightmapType, SpawnPlacements.SpawnPredicate<T> predicate, CallbackInfo info) {
        Heightmap.Types newHeight = null;
        SpawnPlacements.SpawnPredicate<T> newPredicate = null;
        SpawnPlacements.Type newLocation = null;
        boolean changed = false;
        if (SpawnRestrictionReplacements.spawnPlacementTypes.containsKey(type)) {
            changed = true;
            newLocation = SpawnRestrictionReplacements.spawnPlacementTypes.get(type);
        }
        if (SpawnRestrictionReplacements.spawnPlacementTypes.containsKey(type)) {
            changed = true;
            newHeight = SpawnRestrictionReplacements.heightmapTypes.get(type);
        }
        if (SpawnRestrictionReplacements.spawnPredicates.containsKey(type)) {
            changed = true;
            newPredicate = (SpawnPlacements.SpawnPredicate<T>) SpawnRestrictionReplacements.spawnPredicates.get(type);
        }
        if (changed) {
            if (newHeight == null) {
                newHeight = heightmapType;
            }
            if (newLocation == null) {
                newLocation = location;
            }
            if (newPredicate == null) {
                newPredicate = predicate;
            }
            info.cancel();
            SpawnPlacements.Data entry = DATA_BY_TYPE.put(type, new SpawnPlacements.Data(newHeight, newLocation, newPredicate));
        }
    }

}
