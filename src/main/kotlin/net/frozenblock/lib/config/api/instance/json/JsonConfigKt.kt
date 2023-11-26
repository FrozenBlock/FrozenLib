@file:JvmName("JsonConfigKt")

package net.frozenblock.lib.config.api.instance.json

import com.mojang.datafixers.DataFixer
import net.frozenblock.lib.config.api.instance.Config
import net.frozenblock.lib.config.api.instance.json.JsonConfig as RealJsonConfig
import java.nio.file.Path

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

