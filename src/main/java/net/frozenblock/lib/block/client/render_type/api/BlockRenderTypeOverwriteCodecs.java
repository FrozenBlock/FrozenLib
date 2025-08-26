/*
 * Copyright (C) 2024-2025 FrozenBlock
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

package net.frozenblock.lib.block.client.render_type.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.block.client.render_type.impl.BlockRenderTypeOverwrite;
import net.minecraft.resources.ResourceLocation;

@UtilityClass
public class BlockRenderTypeOverwriteCodecs {
	public static final Codec<BlockRenderTypeOverwrite> BLOCK_RENDER_TYPE_OVERWRITE = RecordCodecBuilder.create(instance ->
		instance.group(
			ResourceLocation.CODEC.fieldOf("block").forGetter(BlockRenderTypeOverwrite::getBlockName),
			BlockRenderTypeOverwrite.RenderTypeOverwrite.CODEC.fieldOf("render_type").forGetter(BlockRenderTypeOverwrite::getRenderTypeOverwrite)
		).apply(instance, BlockRenderTypeOverwrite::new)
	);
}
