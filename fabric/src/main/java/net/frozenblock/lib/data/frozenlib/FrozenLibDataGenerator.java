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

package net.frozenblock.lib.data.frozenlib;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.block.api.fire.FireTypes;
import net.frozenblock.lib.data.frozenlib.tag.FrozenLibBlockTagsProvider;
import net.frozenblock.lib.data.frozenlib.tag.FrozenLibEntityTypeTagsProvider;
import net.frozenblock.lib.registry.FrozenLibFabricRegistries;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.core.RegistrySetBuilder;

public final class FrozenLibDataGenerator implements DataGeneratorEntrypoint {

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		final FabricDataGenerator.Pack pack = generator.createPack();

		pack.addProvider(FrozenLibRegistryProvider::new);
		pack.addProvider(FrozenLibBlockTagsProvider::new);
		pack.addProvider(FrozenLibEntityTypeTagsProvider::new);
	}

	@Override
	public void buildRegistry(RegistrySetBuilder builder) {
		builder.add(FrozenLibRegistries.FIRE_TYPE, FireTypes::bootstrap);
	}

	@Override
	public String getEffectiveModId() {
		return FrozenLibConstants.MOD_ID;
	}
}
