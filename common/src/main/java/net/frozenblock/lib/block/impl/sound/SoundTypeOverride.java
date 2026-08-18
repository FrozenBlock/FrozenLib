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
import net.frozenblock.lib.block.api.sound.SoundTypeCodecs;
import net.frozenblock.lib.config.v2.entry.predicates.ConfigPredicate;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

public class SoundTypeOverride {
	public static final Codec<SoundTypeOverride> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
		RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("blocks").forGetter(override -> override.blocks),
		SoundTypeCodecs.SOUND_TYPE.fieldOf("sound_type").forGetter(override -> override.soundType),
		ConfigPredicate.CODEC.optionalFieldOf("config_predicate").forGetter(override -> override.configPredicate)
	).apply(instance, SoundTypeOverride::new));
	private final HolderSet<Block> blocks;
	private final SoundType soundType;
	private final Optional<ConfigPredicate> configPredicate;

	public SoundTypeOverride(HolderSet<Block> blocks, SoundType soundType, Optional<ConfigPredicate> configPredicate) {
		this.blocks = blocks;
		this.soundType = soundType;
		this.configPredicate = configPredicate;
	}

	public SoundType soundType() {
		return this.soundType;
	}

	public HolderSet<Block> blocks() {
		return this.blocks;
	}

	public boolean enabled() {
		return this.configPredicate.isPresent();
	}

	public boolean matches(BlockState state) {
		return this.configPredicate.map(ConfigPredicate::test).orElse(true) && state.is(this.blocks);
	}
}
