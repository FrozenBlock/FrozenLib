/*
 * Copyright 2022 FrozenBlock
 * This file is part of FrozenLib.
 *
 * FrozenLib is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * FrozenLib is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with FrozenLib. If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.sound;

import java.util.HashMap;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.registry.FrozenRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;

public class StartingSounds {

    /**
     * Use this to associate a Starting Sound to a {@link ResourceKey} for later use.
     */
    public static HashMap<ResourceKey<?>, SoundEvent> startingSounds = new HashMap<>();

    public static final SoundEvent EMPTY_SOUND = register("empty_sound");

    public static SoundEvent register(String key) {
        return Registry.register(FrozenRegistry.STARTING_SOUND, key, new SoundEvent(FrozenMain.id(key)));
    }

}
