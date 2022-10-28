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

package org.quiltmc.qsl.frozenblock.misc.datafixerupper.impl;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.frozenblock.lib.FrozenMain;
import org.jetbrains.annotations.ApiStatus;

/**
 * Modified to work on Fabric
 */
@ApiStatus.Internal
public final class ServerFreezer {

    public static void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            FrozenMain.log("[Quilt DFU API] Serverside DataFixer Registry is about to freeze", true);
            QuiltDataFixesInternals.get().freeze();
            FrozenMain.log("[Quilt DFU API] Serverside DataFixer Registry was frozen", true);
        });
    }
}