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

package net.frozenblock.lib.testmod;

import net.fabricmc.api.ClientModInitializer;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.menu.api.SplashTextEvents;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.frozenblock.lib.sound.client.impl.FlyBySoundHub;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityTypes;

@ClientOnly
public final class FrozenTestClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        FlyBySoundHub.AUTO_ENTITIES_AND_SOUNDS.put(EntityTypes.ARROW, new FlyBySoundHub.FlyBySound(1F, 1F, SoundSource.NEUTRAL, SoundEvents.AXE_SCRAPE));
		SplashTextEvents.ADD_SOURCE_FILES.register(sourceFiles -> sourceFiles.add(FrozenLibConstants.id("texts/splashes.txt")));
    }
}
