package net.frozenblock.lib.mixin.worldgen;

import com.mojang.datafixers.util.Either;
import net.frozenblock.lib.replacements_and_lists.StructurePoolElementIdReplacements;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SinglePoolElement.class)
public abstract class SinglePoolElementMixin {
    /** Allows us to change which namespace to use when looking for structure files. */

    @Shadow @Final @Mutable
    public Either<ResourceLocation, StructureTemplate> template;

    /*@Inject(method = "<init>", at = @At("TAIL"))
    public void init(Either<ResourceLocation, StructureTemplate> template, Holder<StructureProcessorList> processors, StructureTemplatePool.Projection projection, CallbackInfo info) {
        if (template.left().isPresent()) {
            ResourceLocation id = template.left().get();
            if (StructurePoolElementIdReplacements.resourceLocationReplacements.containsKey(id)) {
                this.template = Either.left(StructurePoolElementIdReplacements.resourceLocationReplacements.get(id));
            }
        }
    }*/

    @Inject(method = "getTemplate", at = @At("HEAD"), cancellable = true)
    private void getTemplate(StructureTemplateManager structureTemplateManager, CallbackInfoReturnable<StructureTemplate> info) {
        if (template.left().isPresent()) {
            ResourceLocation id = template.left().get();
            if (StructurePoolElementIdReplacements.resourceLocationReplacements.containsKey(id)) {
                info.cancel();
                info.setReturnValue(structureTemplateManager.getOrCreate(StructurePoolElementIdReplacements.resourceLocationReplacements.get(id)));
            }
        }
    }

}
