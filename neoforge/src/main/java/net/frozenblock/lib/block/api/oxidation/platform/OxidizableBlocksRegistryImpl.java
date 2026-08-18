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

package net.frozenblock.lib.block.api.oxidation.platform;

/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Objects;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.WeatheringCopperCollection;
import net.minecraft.world.level.block.state.BlockState;

public final class OxidizableBlocksRegistryImpl {

	public static void registerNextStage(Block from, Block to) {
		Objects.requireNonNull(from, "Oxidizable block cannot be null!");
		Objects.requireNonNull(to, "Oxidizable block cannot be null!");
		WeatheringCopper.NEXT_BY_BLOCK.get().put(from, to);
		refreshRandomTickCache(from);
		refreshRandomTickCache(to);
	}

	public static void registerWaxable(Block unwaxed, Block waxed) {
		Objects.requireNonNull(unwaxed, "Unwaxed block cannot be null!");
		Objects.requireNonNull(waxed, "Waxed block cannot be null!");
		HoneycombItem.WAXABLES.get().put(unwaxed, waxed);
	}

	public static void registerWeatheringCopperBlocks(WeatheringCopperCollection<Block> copperBlocks) {
		Objects.requireNonNull(copperBlocks, "copperBlocks cannot be null!");
		copperBlocks.weathering().progressMapping(OxidizableBlocksRegistryImpl::registerNextStage);
		copperBlocks.zipUnwaxedWaxed(OxidizableBlocksRegistryImpl::registerWaxable);
	}

	private static void refreshRandomTickCache(Block block) {
		for (BlockState state : block.getStateDefinition().getPossibleStates()) {
			state.initCache(); // todo optimize
		}
	}
}
