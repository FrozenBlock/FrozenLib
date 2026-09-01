/*
 * Copyright 2024-2026 The Quilt Project
 * Copyright 2024-2026 FrozenBlock
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

package net.fabricmc.frozenblock.datafixer.impl.client;

import net.frozenblock.lib.FrozenLibLogUtils;
import net.frozenblock.lib.event.api.events.client.ClientLifecycleEvents;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import org.jetbrains.annotations.ApiStatus;
import net.fabricmc.frozenblock.datafixer.impl.FabricDataFixesInternals;

@ClientOnly
@ApiStatus.Internal
public final class ClientFreezer {

    public static void onSetupClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            FrozenLibLogUtils.log("[Fabric DFU API] Clientside DataFixer Registry is about to freeze", true);
            FabricDataFixesInternals.get().freeze();
            FrozenLibLogUtils.log("[Fabric DFU API] Clientside DataFixer Registry was frozen", true);
        });
    }
}
