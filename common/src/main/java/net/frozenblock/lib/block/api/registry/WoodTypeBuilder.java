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
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

public final class WoodTypeBuilder {
	private SoundType soundType = SoundType.WOOD;
	private SoundType hangingSignSoundType = SoundType.HANGING_SIGN;
	private SoundEvent fenceGateCloseSound = SoundEvents.FENCE_GATE_CLOSE;
	private SoundEvent fenceGateOpenSound = SoundEvents.FENCE_GATE_OPEN;

	public WoodTypeBuilder() {}

	public WoodTypeBuilder soundType(SoundType soundType) {
		this.soundType = soundType;
		return this;
	}

	public WoodTypeBuilder hangingSignSoundType(SoundType hangingSignSoundType) {
		this.hangingSignSoundType = hangingSignSoundType;
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
			.soundType(builder.soundType)
			.hangingSignSoundType(builder.hangingSignSoundType)
			.fenceGateCloseSound(builder.fenceGateCloseSound)
			.fenceGateOpenSound(builder.fenceGateOpenSound);
	}

	public static WoodTypeBuilder copyOf(WoodType type) {
		return new WoodTypeBuilder()
			.soundType(type.soundType())
			.hangingSignSoundType(type.hangingSignSoundType())
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
			this.soundType,
			this.hangingSignSoundType,
			this.fenceGateCloseSound,
			this.fenceGateOpenSound
		);
	}
}
