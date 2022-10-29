/*
 * Copyright 2022 FrozenBlock
 * This file is part of FrozenLib.
 *
 * FrozenLib is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * FrozenLib is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with FrozenLib. If not, see <https://www.gnu.org/licenses/>.
 */

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
