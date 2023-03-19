/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.worldgen.feature.features.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class ColumnWithDiskFeatureConfig implements FeatureConfiguration {
    public static final Codec<ColumnWithDiskFeatureConfig> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    BlockState.CODEC.fieldOf("columnBlock").forGetter((config) -> config.columnBlock),
                    IntProvider.NON_NEGATIVE_CODEC.fieldOf("radius").forGetter((config) -> config.radius),
                    IntProvider.NON_NEGATIVE_CODEC.fieldOf("height").forGetter((config) -> config.height),
                    IntProvider.NON_NEGATIVE_CODEC.fieldOf("height2").forGetter((config) -> config.height2),
                    RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("replaceable").forGetter((config) -> config.replaceable),
                    RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("diskBlocks").forGetter((config) -> config.diskBlocks)
            ).apply(instance, ColumnWithDiskFeatureConfig::new));

    public final BlockState columnBlock;
    public final IntProvider radius;
    public final IntProvider height;
    public final IntProvider height2;
    public final HolderSet<Block> replaceable;
    public final HolderSet<Block> diskBlocks;

    private static DataResult<Block> validateBlock(Block block) {
        return DataResult.success(block);
    }

    public ColumnWithDiskFeatureConfig(BlockState columnBlock, IntProvider radius, IntProvider height, IntProvider height2, HolderSet<Block> replaceable, HolderSet<Block> diskBlocks) {
        this.columnBlock = columnBlock;
        this.radius = radius;
        this.height = height;
        this.height2 = height2;
        this.replaceable = replaceable;
        this.diskBlocks = diskBlocks;
    }

}
