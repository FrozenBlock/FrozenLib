package net.frozenblock.lib.mixin.server;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.LegacySinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LegacySinglePoolElement.class)
public interface LegacySinglePoolElementAccessor {

    @Invoker("<init>")
    static LegacySinglePoolElement legacySinglePoolElement(
            Either<ResourceLocation, StructureTemplate> either,
            Holder<StructureProcessorList> holder,
            StructureTemplatePool.Projection projection) {
        throw new AssertionError();
    }

}
