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

@file:JvmName("JsonConfigKt")

package net.frozenblock.lib.config.api.instance.json

import com.mojang.datafixers.DataFixer
import net.frozenblock.lib.config.api.instance.Config
import java.nio.file.Path
import net.frozenblock.lib.config.api.instance.json.JsonConfig as RealJsonConfig

fun <T> JsonConfig(
    modId: String,
    config: Class<T>,
    type: JsonType = JsonType.JSON,
    path: Path = Config.makePath(modId, type.serializedName),
    supportsModification: Boolean = true,
    dataFixer: DataFixer? = null,
    version: Int? = null
): RealJsonConfig<T> {
    return RealJsonConfig(
        modId,
        config,
        path,
        type,
        supportsModification,
        dataFixer,
        version
    )
}
