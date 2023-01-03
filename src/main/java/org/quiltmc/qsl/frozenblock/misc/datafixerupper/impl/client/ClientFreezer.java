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

package org.quiltmc.qsl.frozenblock.misc.datafixerupper.impl.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.frozenblock.lib.FrozenMain;
import org.jetbrains.annotations.ApiStatus;
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.impl.QuiltDataFixesInternals;

/**
 * Modified to work on Fabric
 */
@Environment(EnvType.CLIENT)
@ApiStatus.Internal
public final class ClientFreezer {

    public static void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            FrozenMain.log("[Quilt DFU API] Clientside DataFixer Registry is about to freeze", true);
            QuiltDataFixesInternals.get().freeze();
            FrozenMain.log("[Quilt DFU API] Clientside DataFixer Registry was frozen", true);
        });
    }
}
