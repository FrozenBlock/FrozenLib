/*
 * Copyright 2024 FrozenBlock
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

package net.frozenblock.lib.core.api

import net.minecraft.resources.ResourceLocation
import java.net.URI

fun String.uri(): URI = URI.create(this)

fun vanillaId(path: String): ResourceLocation = ResourceLocation(path)

val String.asResourceLocation: ResourceLocation
    get() {
        return ResourceLocation(this)
    }
