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

package net.fabricmc.frozenblock.datafixer.impl;

import net.frozenblock.lib.FrozenLibLogUtils;
import net.frozenblock.lib.event.api.events.LifecycleEvents;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class ServerFreezer {

    public static void onInitialize() {
        LifecycleEvents.SERVER_STARTING.register(server -> {
            FrozenLibLogUtils.log("[Fabric DFU API] Serverside DataFixer Registry is about to freeze", true);
            FabricDataFixesInternals.get().freeze();
            FrozenLibLogUtils.log("[Fabric DFU API] Serverside DataFixer Registry was frozen", true);
        });
    }
}
