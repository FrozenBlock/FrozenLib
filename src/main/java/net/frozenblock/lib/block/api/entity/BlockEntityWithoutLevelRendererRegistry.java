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

package net.frozenblock.lib.block.api.entity;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

public class BlockEntityWithoutLevelRendererRegistry {
	private static final Object2ObjectLinkedOpenHashMap<Block, BlockEntity> BLOCK_TO_BLOCK_ENTITY_MAP = new Object2ObjectLinkedOpenHashMap<>();

	public static void register(Block block, @NotNull BlockEntityType<?> blockEntityType) {
		BLOCK_TO_BLOCK_ENTITY_MAP.put(block, blockEntityType.create(BlockPos.ZERO, block.defaultBlockState()));
	}

	public static Optional<BlockEntity> getBlockEntity(Block block) {
		return Optional.ofNullable(BLOCK_TO_BLOCK_ENTITY_MAP.get(block));
	}

	public static boolean hasBlock(Block block) {
		return BLOCK_TO_BLOCK_ENTITY_MAP.containsKey(block);
	}

}
