/*
 * Copyright 2022 QuiltMC
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

package org.quiltmc.qsl.frozenblock.worldgen.surface_rule.impl;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.entrypoints.CommonEventEntrypoint;
import net.frozenblock.lib.events.FrozenEvents;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;

import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.api.ModInitializer;
import org.quiltmc.qsl.frozenblock.worldgen.surface_rule.api.SurfaceRuleEvents;

import java.util.Map;

/**
 * Modified to work on Fabric
 */
@ApiStatus.Internal
public class QuiltSurfaceRuleInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        Registry.register(Registry.RULE, FrozenMain.id("reloadable_sequence"),
                ReloadableSequenceMaterialRule.RULE_CODEC.codec()
        );
    }
}
