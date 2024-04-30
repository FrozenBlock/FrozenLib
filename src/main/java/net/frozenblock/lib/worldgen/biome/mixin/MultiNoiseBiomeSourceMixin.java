/*
 * Copyright (C) 2024 FrozenBlock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.worldgen.biome.mixin;

import net.frozenblock.lib.worldgen.biome.api.FrozenBiomeSourceAccess;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MultiNoiseBiomeSource.class)
public class MultiNoiseBiomeSourceMixin implements FrozenBiomeSourceAccess {

    @Unique
    private boolean frozenLib$modifyBiomeEntries = true;

	@Unique
    @Override
    public void frozenLib_setModifyBiomeEntries(boolean modifyBiomeEntries) {
        this.frozenLib$modifyBiomeEntries = modifyBiomeEntries;
    }

	@Unique
    @Override
    public boolean frozenLib_shouldModifyBiomeEntries() {
        return this.frozenLib$modifyBiomeEntries;
    }
}
