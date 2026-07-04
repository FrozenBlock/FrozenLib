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

package net.frozenblock.lib.registry.api;

import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public final class BlockSetTypeBuilder {
	private boolean openableByHand = true;
	private boolean openableByWindCharge = true;
	private boolean buttonActivatedByArrows = true;
	private BlockSetType.PressurePlateSensitivity pressurePlateActivationRule = BlockSetType.PressurePlateSensitivity.EVERYTHING;
	private SoundType soundType = SoundType.WOOD;
	private SoundEvent doorCloseSound = SoundEvents.WOODEN_DOOR_CLOSE;
	private SoundEvent doorOpenSound = SoundEvents.WOODEN_DOOR_OPEN;
	private SoundEvent trapdoorCloseSound = SoundEvents.WOODEN_TRAPDOOR_CLOSE;
	private SoundEvent trapdoorOpenSound = SoundEvents.WOODEN_TRAPDOOR_OPEN;
	private SoundEvent pressurePlateClickOffSound = SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_OFF;
	private SoundEvent pressurePlateClickOnSound = SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_ON;
	private SoundEvent buttonClickOffSound = SoundEvents.WOODEN_BUTTON_CLICK_OFF;
	private SoundEvent buttonClickOnSound = SoundEvents.WOODEN_BUTTON_CLICK_ON;

	public BlockSetTypeBuilder() {
	}

	public BlockSetTypeBuilder openableByHand(boolean openableByHand) {
		this.openableByHand = openableByHand;
		return this;
	}

	public BlockSetTypeBuilder openableByWindCharge(boolean openableByWindCharge) {
		this.openableByWindCharge = openableByWindCharge;
		return this;
	}

	public BlockSetTypeBuilder buttonActivatedByArrows(boolean buttonActivatedByArrows) {
		this.buttonActivatedByArrows = buttonActivatedByArrows;
		return this;
	}

	public BlockSetTypeBuilder pressurePlateActivationRule(BlockSetType.PressurePlateSensitivity pressurePlateActivationRule) {
		this.pressurePlateActivationRule = pressurePlateActivationRule;
		return this;
	}

	public BlockSetTypeBuilder soundType(SoundType soundType) {
		this.soundType = soundType;
		return this;
	}

	public BlockSetTypeBuilder doorCloseSound(SoundEvent doorCloseSound) {
		this.doorCloseSound = doorCloseSound;
		return this;
	}

	public BlockSetTypeBuilder doorOpenSound(SoundEvent doorOpenSound) {
		this.doorOpenSound = doorOpenSound;
		return this;
	}

	public BlockSetTypeBuilder trapdoorCloseSound(SoundEvent trapdoorCloseSound) {
		this.trapdoorCloseSound = trapdoorCloseSound;
		return this;
	}

	public BlockSetTypeBuilder trapdoorOpenSound(SoundEvent trapdoorOpenSound) {
		this.trapdoorOpenSound = trapdoorOpenSound;
		return this;
	}

	public BlockSetTypeBuilder pressurePlateClickOffSound(SoundEvent pressurePlateClickOffSound) {
		this.pressurePlateClickOffSound = pressurePlateClickOffSound;
		return this;
	}

	public BlockSetTypeBuilder pressurePlateClickOnSound(SoundEvent pressurePlateClickOnSound) {
		this.pressurePlateClickOnSound = pressurePlateClickOnSound;
		return this;
	}

	public BlockSetTypeBuilder buttonClickOffSound(SoundEvent buttonClickOffSound) {
		this.buttonClickOffSound = buttonClickOffSound;
		return this;
	}

	public BlockSetTypeBuilder buttonClickOnSound(SoundEvent buttonClickOnSound) {
		this.buttonClickOnSound = buttonClickOnSound;
		return this;
	}

	public static BlockSetTypeBuilder copyOf(BlockSetTypeBuilder builder) {
		return new BlockSetTypeBuilder()
			.openableByHand(builder.openableByHand)
			.openableByWindCharge(builder.openableByWindCharge)
			.buttonActivatedByArrows(builder.buttonActivatedByArrows)
			.pressurePlateActivationRule(builder.pressurePlateActivationRule)
			.soundType(builder.soundType)
			.doorCloseSound(builder.doorCloseSound)
			.doorOpenSound(builder.doorOpenSound)
			.trapdoorCloseSound(builder.trapdoorCloseSound)
			.trapdoorOpenSound(builder.trapdoorOpenSound)
			.pressurePlateClickOffSound(builder.pressurePlateClickOffSound)
			.pressurePlateClickOnSound(builder.pressurePlateClickOnSound)
			.buttonClickOffSound(builder.buttonClickOffSound)
			.buttonClickOnSound(builder.buttonClickOnSound);
	}

	public static BlockSetTypeBuilder copyOf(BlockSetType type) {
		return new BlockSetTypeBuilder()
			.openableByHand(type.canOpenByHand())
			.openableByWindCharge(type.canOpenByWindCharge())
			.buttonActivatedByArrows(type.canButtonBeActivatedByArrows())
			.pressurePlateActivationRule(type.pressurePlateSensitivity())
			.soundType(type.soundType())
			.doorCloseSound(type.doorClose())
			.doorOpenSound(type.doorOpen())
			.trapdoorCloseSound(type.trapdoorClose())
			.trapdoorOpenSound(type.trapdoorOpen())
			.pressurePlateClickOffSound(type.pressurePlateClickOff())
			.pressurePlateClickOnSound(type.pressurePlateClickOn())
			.buttonClickOffSound(type.buttonClickOff())
			.buttonClickOnSound(type.buttonClickOn());
	}

	public BlockSetType register(Identifier id) {
		return BlockSetType.register(this.build(id));
	}

	public BlockSetType build(Identifier id) {
		return new BlockSetType(
			id.toString(),
			this.openableByHand,
			this.openableByWindCharge,
			this.buttonActivatedByArrows,
			this.pressurePlateActivationRule,
			this.soundType,
			this.doorCloseSound,
			this.doorOpenSound,
			this.trapdoorCloseSound,
			this.trapdoorOpenSound,
			this.pressurePlateClickOffSound,
			this.pressurePlateClickOnSound,
			this.buttonClickOffSound,
			this.buttonClickOnSound
		);
	}
}
