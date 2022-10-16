package net.frozenblock.lib.mixin.worldgen.biome;

import net.frozenblock.lib.worldgen.biome.api.FrozenBiomeSourceAccess;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MultiNoiseBiomeSource.class)
public class MultiNoiseBiomeSourceMixin implements FrozenBiomeSourceAccess {

    @Unique
    private boolean frozenLib$modifyBiomeEntries = true;

    @Override
    public void frozenLib_setModifyBiomeEntries(boolean modifyBiomeEntries) {
        this.frozenLib$modifyBiomeEntries = modifyBiomeEntries;
    }

    @Override
    public boolean frozenLib_shouldModifyBiomeEntries() {
        return this.frozenLib$modifyBiomeEntries;
    }
}
