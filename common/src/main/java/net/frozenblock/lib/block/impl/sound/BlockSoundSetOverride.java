/*
 * Copyright (C) 2025-2026 FrozenBlock
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

package net.frozenblock.lib.block.impl.sound;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.frozenblock.lib.config.v2.entry.predicates.ConfigPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.sounds.BlockSoundSet;
import net.minecraft.world.level.block.state.BlockState;

public class BlockSoundSetOverride {
	public static final Codec<BlockSoundSetOverride> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
		RegistryCodecs.holderSet(Registries.BLOCK).fieldOf("blocks").forGetter(override -> override.blocks),
		RegistryCodecs.holder(Registries.BLOCK_SOUND_SET, BlockSoundSet.DIRECT_CODEC).fieldOf("sound_set").forGetter(override -> override.soundSet),
		ConfigPredicate.HOLDER_CODEC.optionalFieldOf("config_predicate").forGetter(override -> override.configPredicate)
	).apply(instance, BlockSoundSetOverride::new));
	private final HolderSet<Block> blocks;
	private final Holder<BlockSoundSet> soundSet;
	private final Optional<Holder<ConfigPredicate>> configPredicate;

	public BlockSoundSetOverride(HolderSet<Block> blocks, Holder<BlockSoundSet> soundSet, Optional<Holder<ConfigPredicate>> configPredicate) {
		this.blocks = blocks;
		this.soundSet = soundSet;
		this.configPredicate = configPredicate;
	}

	public Holder<BlockSoundSet> soundSet() {
		return this.soundSet;
	}

	public HolderSet<Block> blocks() {
		return this.blocks;
	}

	public boolean enabled() {
		return this.configPredicate.isPresent();
	}

	public boolean matches(BlockState state) {
		return this.configPredicate.map(Holder::value).map(ConfigPredicate::test).orElse(true) && state.is(this.blocks);
	}
}
