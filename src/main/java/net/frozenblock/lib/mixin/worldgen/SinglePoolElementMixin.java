package net.frozenblock.lib.mixin.worldgen;

import com.mojang.datafixers.util.Either;
import net.frozenblock.lib.replacements_and_lists.StructurePoolElementIdReplacements;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SinglePoolElement.class)
public class SinglePoolElementMixin {

    @Shadow @Final @Mutable
    protected Either<ResourceLocation, StructureTemplate> template;

    @Inject(method = "<init>(Lcom/mojang/datafixers/util/Either;Lnet/minecraft/core/Holder;Lnet/minecraft/world/level/levelgen/structure/pools/StructureTemplatePool$Projection;)V", at = @At("TAIL"))
    public void init(Either<ResourceLocation, StructureTemplate> template, Holder<StructureProcessorList> processors, StructureTemplatePool.Projection projection, CallbackInfo info) {
        if (template.left().isPresent()) {
            ResourceLocation id = template.left().get();
            if (StructurePoolElementIdReplacements.resourceLocationReplacements.containsKey(id)) {
                this.template = Either.left(StructurePoolElementIdReplacements.resourceLocationReplacements.get(id));
            }
        }
    }

}
