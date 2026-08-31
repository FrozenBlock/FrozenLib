/*
 * Copyright (C) 2024-2026 FrozenBlock
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

package net.frozenblock.lib.block.client.api.platform;

import java.util.Arrays;
import java.util.List;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.frozenblock.lib.platform.api.registry.DeferredBlock;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.world.level.block.Block;

@ClientOnly
public final class BlockColorRegistryImpl {

	public static void register(List<BlockTintSource> tintSources, DeferredBlock<?>... blocks) {
		BlockColorRegistry.register(tintSources, Arrays.stream(blocks).map(DeferredBlock::get).toArray(Block[]::new));
	}
}
