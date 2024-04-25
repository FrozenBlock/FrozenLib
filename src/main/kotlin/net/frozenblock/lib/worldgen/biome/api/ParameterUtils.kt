/*
 * Copyright 2023 The Quilt Project
 * Copyright 2023 FrozenBlock
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
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.worldgen.biome.api

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.level.biome.Climate

data class MutableParameter(
    @JvmField var min: Long?,
    @JvmField var max: Long?
) {
    companion object {
        @JvmField
        val CODEC: Codec<MutableParameter> = ExtraCodecs.intervalCodec(
            Codec.floatRange(-2.0F, 2.0F),
            "min",
            "max",
            { float, float2 ->
                if (float.compareTo(float2) > 0)
                    DataResult.error { "Cannon construct interval, min > max ($float > $float2)"}
                else
                    DataResult.success(MutableParameter(Climate.quantizeCoord(float), Climate.quantizeCoord(float2)))
            },
            { range -> Climate.unquantizeCoord(range.min ?: 0L) },
            { parameter -> Climate.unquantizeCoord(parameter.max ?: 0L) }
        )
    }

    fun toImmutable(): Climate.Parameter? = if (min == null || max == null) null else Climate.Parameter(min!!, max!!)
}

fun Climate.Parameter.mutable(): MutableParameter = MutableParameter(min, max)

data class MutableParameterPoint(
    @JvmField var temperature: MutableParameter?,
    @JvmField var humidity: MutableParameter?,
    @JvmField var continentalness: MutableParameter?,
    @JvmField var erosion: MutableParameter?,
    @JvmField var depth: MutableParameter?,
    @JvmField var weirdness: MutableParameter?,
    @JvmField var offset: Long?
) {
    companion object {
        @JvmField
        val CODEC: Codec<MutableParameterPoint> = RecordCodecBuilder.create { instance ->
            instance.group(
                MutableParameter.CODEC.fieldOf("temperature").forGetter(MutableParameterPoint::temperature),
                MutableParameter.CODEC.fieldOf("humidity").forGetter(MutableParameterPoint::humidity),
                MutableParameter.CODEC.fieldOf("continentalness").forGetter(MutableParameterPoint::continentalness),
                MutableParameter.CODEC.fieldOf("erosion").forGetter(MutableParameterPoint::erosion),
                MutableParameter.CODEC.fieldOf("depth").forGetter(MutableParameterPoint::depth),
                MutableParameter.CODEC.fieldOf("weirdness").forGetter(MutableParameterPoint::weirdness),
                Codec.floatRange(0.0F, 1.0F).fieldOf("offset").xmap(Climate::quantizeCoord, Climate::unquantizeCoord).forGetter(
                    MutableParameterPoint::offset)
            ).apply(instance, ::MutableParameterPoint)
        }
    }

    fun toImmutable(): Climate.ParameterPoint? {
        val temperature = this.temperature?.toImmutable() ?: return null
        val humidity = this.humidity?.toImmutable() ?: return null
        val continentalness = this.continentalness?.toImmutable() ?: return null
        val erosion = this.erosion?.toImmutable() ?: return null
        val depth = this.depth?.toImmutable() ?: return null
        val weirdness = this.weirdness?.toImmutable() ?: return null
        val offset = this.offset ?: return null
        return Climate.ParameterPoint(
            temperature,
            humidity,
            continentalness,
            erosion,
            depth,
            weirdness,
            offset
        )
    }
}

fun Climate.ParameterPoint.mutable(): MutableParameterPoint = MutableParameterPoint(temperature.mutable(), humidity.mutable(), continentalness.mutable(), erosion.mutable(), depth.mutable(), weirdness.mutable(), offset)
