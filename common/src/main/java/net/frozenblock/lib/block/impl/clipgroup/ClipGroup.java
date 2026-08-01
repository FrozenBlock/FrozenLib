/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.block.impl.clipgroup;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.registry.FrozenLibRegistries;import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.core.registries.codec.RegistryFixedCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public record ClipGroup(HolderSet<Block> blocks) {
	public static final Codec<ClipGroup> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
		RegistryCodecs.holderSet(Registries.BLOCK).fieldOf("blocks").forGetter(ClipGroup::blocks)
	).apply(instance, ClipGroup::new));
	public static final Codec<Holder<ClipGroup>> CODEC = RegistryFixedCodec.create(FrozenLibRegistries.CLIP_GROUP);
	public static final StreamCodec<RegistryFriendlyByteBuf, Holder<ClipGroup>> STREAM_CODEC = ByteBufCodecs.holderRegistry(FrozenLibRegistries.CLIP_GROUP);

	public boolean contains(BlockState state) {
		return state.is(this.blocks);
	}
}
