/*
 * Copyright 2023 FrozenBlock
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
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.worldgen.biome.api.parameters;

import net.minecraft.world.level.biome.Climate;

public final class Weirdness {

    public static final Climate.Parameter[] weirdnesses = new Climate.Parameter[]{
            Climate.Parameter.span(-1.0F, -0.93333334F), // 0
            Climate.Parameter.span(-0.93333334F, -0.7666667F), // 1
            Climate.Parameter.span(-0.7666667F, -0.56666666F), // 2
            Climate.Parameter.span(-0.56666666F, -0.4F), // 3
            Climate.Parameter.span(-0.4F, -0.26666668F), // 4
            Climate.Parameter.span(-0.26666668F, -0.05F), // 5
            Climate.Parameter.span(-0.05F, 0.05F), // 6
            Climate.Parameter.span(0.05F, 0.26666668F), // 7
            Climate.Parameter.span(0.26666668F, 0.4F), // 8
            Climate.Parameter.span(0.4F, 0.56666666F), // 9
            Climate.Parameter.span(0.56666666F, 0.7666667F), // 10
            Climate.Parameter.span(0.7666667F, 0.93333334F), // 11
            Climate.Parameter.span(0.93333334F, 1.0F) // 12
    };

    public static final Climate.Parameter MID_SLICE_NORMAL_ASCENDING = weirdnesses[0];
    public static final Climate.Parameter HIGH_SLICE_NORMAL_ASCENDING = weirdnesses[1];
    public static final Climate.Parameter PEAK_NORMAL = weirdnesses[2];
    public static final Climate.Parameter HIGH_SLICE_NORMAL_DESCENDING = weirdnesses[3];
    public static final Climate.Parameter MID_SLICE_NORMAL_DESCENDING = weirdnesses[4];
    public static final Climate.Parameter LOW_SLICE_NORMAL_DESCENDING = weirdnesses[5];
    public static final Climate.Parameter VALLEY = weirdnesses[6];
    public static final Climate.Parameter LOW_SLICE_VARIANT_ASCENDING = weirdnesses[7];
    public static final Climate.Parameter MID_SLICE_VARIANT_ASCENDING = weirdnesses[8];
    public static final Climate.Parameter HIGH_SLICE_VARIANT_ASCENDING = weirdnesses[9];
    public static final Climate.Parameter PEAK_VARIANT = weirdnesses[10];
    public static final Climate.Parameter HIGH_SLICE_VARIANT_DESCENDING = weirdnesses[11];
    public static final Climate.Parameter MID_SLICE_VARIANT_DESCENDING = weirdnesses[12];
    public static final Climate.Parameter FULL_RANGE = OverworldBiomeBuilderParameters.FULL_RANGE;
}
