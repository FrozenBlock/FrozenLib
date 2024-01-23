/*
 * Copyright 2023-2024 FrozenBlock
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

import blue.endless.jankson.Comment;
import com.mojang.serialization.Codec;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.config.api.entry.TypedEntry;
import net.frozenblock.lib.config.api.entry.TypedEntryType;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.api.instance.json.JsonConfig;
import net.frozenblock.lib.config.api.instance.json.JsonType;
import net.frozenblock.lib.config.api.instance.toml.TomlConfig;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;
import net.frozenblock.lib.testmod.FrozenTestMain;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;

public class TestConfig {

	// Make sure to define TypedEntryTypes at top of class.

	public static final Config<TestConfig> INSTANCE = ConfigRegistry.register(
			new TomlConfig<>(
				FrozenTestMain.MOD_ID,
				TestConfig.class
			)
	);

	@Comment("This is a boolean value.")
	public boolean testToggle = true;

	@Comment("This is an integer value.")
	public int testInt = 69;

	@Comment("This is a long value.")
	public long testLong = 69420L;

	@Comment("This is a float value.")
	public float testFloat = 69.420F;

	@Comment("This is a double value.")
	public double testDouble = 69.4206942069420D;

	@Comment("This is an integer list typed entry.")
	public List<Integer> testIntList = List.of(45);

	@Comment("This is a sound event typed entry.")
	public SoundEvent randomSound = SoundEvents.BEE_LOOP;

	@Comment("This is a Vec3 list typed entry.")
	public List<Vec3> typedVecList = List.of(new Vec3(0, 0, 0), new Vec3(1, 1, 1));

	@Comment("This is a list of doubles")
	public List<Double> doubleList = List.of(1D, 2D, 3D, Math.PI);

	@Comment("Sub menu!")
	public SubMenu subMenu = new SubMenu();

	public static class SubMenu {
		@Comment("Crazy sub option ngl")
		public boolean subOption = true;
	}

	public static TestConfig get() {
		return get(false);
	}

	public static TestConfig get(boolean real) {
		if (real) return INSTANCE.instance();
		return INSTANCE.config();
	}
}
