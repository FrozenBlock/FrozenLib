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

package net.frozenblock.lib.testmod.config;

import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.config.api.entry.TypedEntry;
import net.frozenblock.lib.config.api.entry.TypedEntryType;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.api.instance.json.JsonConfig;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;
import net.frozenblock.lib.testmod.FrozenTestMain;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;
import java.util.List;

public class TestConfig {

	// Make sure to define TypedEntryTypes at top of class.
	public static final TypedEntryType<SoundEvent> SOUND_EVENT = ConfigRegistry.register(
			new TypedEntryType<>(
					FrozenTestMain.MOD_ID,
					SoundEvent.CODEC
			)
	);

	public static final TypedEntryType<List<Vec3>> VEC3_LIST = ConfigRegistry.register(
			new TypedEntryType<>(
					FrozenTestMain.MOD_ID,
					Codec.list(Vec3.CODEC)
			)
	);

	private static final Config<TestConfig> INSTANCE = ConfigRegistry.register(
			new JsonConfig<>(FrozenTestMain.MOD_ID, TestConfig.class, true)
	);

	public boolean testToggle = true;

	public int testInt = 69;

	public long testLong = 69420L;

	public float testFloat = 69.420F;

	public double testDouble = 69.4206942069420D;

	public List<Integer> testIntList = List.of(45);

	public TypedEntry<Boolean> typedBoolean = new TypedEntry<>(
			TypedEntryType.BOOLEAN, true
	);

	public TypedEntry<Integer> typedInt = new TypedEntry<>(
			TypedEntryType.INTEGER, 69
	);

	public TypedEntry<List<Double>> typedDoubleList = new TypedEntry<>(
			TypedEntryType.DOUBLE_LIST, List.of(1D, 2D, 69.69696969696969696969696969420D)
	);

	public TypedEntry<SoundEvent> randomSound = new TypedEntry<>(
			SOUND_EVENT, SoundEvents.BEE_LOOP
	);

	public TypedEntry<List<Vec3>> typedVecList = new TypedEntry<>(
			VEC3_LIST, List.of(new Vec3(0, 0, 0), new Vec3(1, 1, 1))
	);

	public static Config<TestConfig> get() {
		return INSTANCE;
	}

	@Environment(EnvType.CLIENT)
	public Screen makeGui(Screen parent) {
		return null;
		/*return ClientConfig.makeBuilder()
				.title(Component.literal("FrozenLib Testmod"))
				.option(
						this.testToggle,
						OptionType.BUTTON,
						Component.translatable("frozenlib_testmod.config.test_toggle"),
						newValue -> this.testToggle = newValue
				)
				.save(INSTANCE::save)
				.build()
				.makeScreen(parent);*/
	}
}
