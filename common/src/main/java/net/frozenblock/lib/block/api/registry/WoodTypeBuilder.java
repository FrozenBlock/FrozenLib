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

package net.frozenblock.lib.block.api.registry;

import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.sounds.BlockSoundSet;
import net.minecraft.world.level.block.sounds.BlockSoundSets;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

public final class WoodTypeBuilder {
	private ResourceKey<BlockSoundSet> blockSoundSet = BlockSoundSets.WOOD;
	private ResourceKey<BlockSoundSet> hangingSignSoundSet = BlockSoundSets.HANGING_SIGN;
	private SoundEvent fenceGateCloseSound = SoundEvents.FENCE_GATE_CLOSE;
	private SoundEvent fenceGateOpenSound = SoundEvents.FENCE_GATE_OPEN;

	public WoodTypeBuilder() {}

	public WoodTypeBuilder blockSoundSet(ResourceKey<BlockSoundSet> blockSoundSet) {
		this.blockSoundSet = blockSoundSet;
		return this;
	}

	public WoodTypeBuilder hangingSignSoundSet(ResourceKey<BlockSoundSet> hangingSignSoundSet) {
		this.hangingSignSoundSet = hangingSignSoundSet;
		return this;
	}

	public WoodTypeBuilder fenceGateCloseSound(SoundEvent fenceGateCloseSound) {
		this.fenceGateCloseSound = fenceGateCloseSound;
		return this;
	}

	public WoodTypeBuilder fenceGateOpenSound(SoundEvent fenceGateOpenSound) {
		this.fenceGateOpenSound = fenceGateOpenSound;
		return this;
	}

	public static WoodTypeBuilder copyOf(WoodTypeBuilder builder) {
		return new WoodTypeBuilder()
			.blockSoundSet(builder.blockSoundSet)
			.hangingSignSoundSet(builder.hangingSignSoundSet)
			.fenceGateCloseSound(builder.fenceGateCloseSound)
			.fenceGateOpenSound(builder.fenceGateOpenSound);
	}

	public static WoodTypeBuilder copyOf(WoodType type) {
		return new WoodTypeBuilder()
			.blockSoundSet(type.blockSoundSet())
			.hangingSignSoundSet(type.hangingSignSoundSet())
			.fenceGateCloseSound(type.fenceGateClose())
			.fenceGateOpenSound(type.fenceGateOpen());
	}

	public WoodType register(Identifier id, BlockSetType blockSetType) {
		return WoodType.register(this.build(id, blockSetType));
	}

	public WoodType build(Identifier id, BlockSetType blockSetType) {
		return new WoodType(
			id.toString(),
			blockSetType,
			this.blockSoundSet,
			this.hangingSignSoundSet,
			this.fenceGateCloseSound,
			this.fenceGateOpenSound
		);
	}
}
