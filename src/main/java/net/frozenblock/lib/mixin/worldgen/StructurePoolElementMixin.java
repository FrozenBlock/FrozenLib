package net.frozenblock.lib.mixin.worldgen;

import com.mojang.datafixers.util.Either;
import net.frozenblock.lib.mixin.server.LegacySinglePoolElementAccessor;
import net.frozenblock.lib.replacements_and_lists.StructurePoolElementIdReplacements;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.world.level.levelgen.structure.pools.LegacySinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(StructurePoolElement.class)
public class StructurePoolElementMixin {
    /** Allows us to change which namespace to use when looking for structure files. */

    @Inject(method = "legacy(Ljava/lang/String;)Ljava/util/Objects/Function;", at = @At("HEAD"), cancellable = true)
    private static void legacy(String id, CallbackInfoReturnable<Function<StructureTemplatePool.Projection, LegacySinglePoolElement>> info) {
        if (StructurePoolElementIdReplacements.resourceLocationReplacements.containsKey(id)) {
            info.cancel();
            info.setReturnValue(projection -> LegacySinglePoolElementAccessor.legacySinglePoolElement(Either.left(StructurePoolElementIdReplacements.resourceLocationReplacements.get(id)), ProcessorLists.EMPTY, projection));
        }
    }

    @Inject(method = "legacy(Ljava/lang/String;Lnet/minecraft/core/Holder;)Ljava/util/Objects/Function;", at = @At("HEAD"), cancellable = true)
    private static void legacy(String id, Holder<StructureProcessorList> processors, CallbackInfoReturnable<Function<StructureTemplatePool.Projection, LegacySinglePoolElement>> info) {
        if (StructurePoolElementIdReplacements.resourceLocationReplacements.containsKey(id)) {
            info.cancel();
            info.setReturnValue(projection -> LegacySinglePoolElementAccessor.legacySinglePoolElement(Either.left(StructurePoolElementIdReplacements.resourceLocationReplacements.get(id)), processors, projection));
        }
    }

    @Inject(method = "single(Ljava/lang/String;)Ljava/util/Objects/Function;", at = @At("HEAD"), cancellable = true)
    private static void single(String id, CallbackInfoReturnable<Function<StructureTemplatePool.Projection, SinglePoolElement>> info) {
        if (StructurePoolElementIdReplacements.resourceLocationReplacements.containsKey(id)) {
            info.cancel();
            info.setReturnValue(projection -> new SinglePoolElement(Either.left(StructurePoolElementIdReplacements.resourceLocationReplacements.get(id)), ProcessorLists.EMPTY, projection));
        }
    }

    @Inject(method = "single(Ljava/lang/String;Lnet/minecraft/core/Holder;)Ljava/util/Objects/Function;", at = @At("HEAD"), cancellable = true)
    private static void single(String id, Holder<StructureProcessorList> processors, CallbackInfoReturnable<Function<StructureTemplatePool.Projection, SinglePoolElement>> info) {
        if (StructurePoolElementIdReplacements.resourceLocationReplacements.containsKey(id)) {
            info.cancel();
            info.setReturnValue(projection -> new SinglePoolElement(Either.left(StructurePoolElementIdReplacements.resourceLocationReplacements.get(id)), processors, projection));
        }
    }

}
