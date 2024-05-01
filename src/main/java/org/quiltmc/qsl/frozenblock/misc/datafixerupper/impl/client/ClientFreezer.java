/*
 * Copyright 2024 The Quilt Project
 * Copyright 2024 FrozenBlock
 * Modified to work on Fabric
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

package org.quiltmc.qsl.frozenblock.misc.datafixerupper.impl.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.frozenblock.lib.FrozenLogUtils;
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
            FrozenLogUtils.log("[Quilt DFU API] Clientside DataFixer Registry is about to freeze", true);
            QuiltDataFixesInternals.get().freeze();
            FrozenLogUtils.log("[Quilt DFU API] Clientside DataFixer Registry was frozen", true);
        });
    }
}
