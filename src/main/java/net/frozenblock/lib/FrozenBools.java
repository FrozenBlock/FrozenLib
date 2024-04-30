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

package net.frozenblock.lib;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.Bootstrap;

public class FrozenBools {

	/**
	 * This is set to true when {@link Bootstrap#bootStrap()} is finished.
	 */
	public static boolean isInitialized;

    public static boolean useNewDripstoneLiquid = false;

	// MOD LOADERS
	public static final boolean IS_FABRIC = hasMod("fabricloader") && !hasMod("quilt_loader") && !hasMod("connector");
	public static final boolean IS_QUILT = hasMod("quilt_loader");
	public static final boolean IS_FORGE = hasMod("connector");

    // EXTERNAL MODS
	public static final boolean HAS_ARCHITECTURY = hasMod("architectury");
	public static final boolean HAS_C2ME = hasMod("c2me");
    public static final boolean HAS_CLOTH_CONFIG = hasMod("cloth-config");
	public static final boolean HAS_INDIUM = hasMod("indium");
	public static final boolean HAS_IRIS = hasMod("iris");
	public static final boolean HAS_MOONLIGHT_LIB = hasMod("moonlight");
    public static final boolean HAS_SIMPLE_COPPER_PIPES = hasMod("copper_pipe");
    public static final boolean HAS_SODIUM = hasMod("sodium");
	public static final boolean HAS_STARLIGHT = hasMod("starlight");
    public static final boolean HAS_TERRABLENDER = hasMod("terrablender");
    public static final boolean HAS_TERRALITH = hasMod("terralith");

	public static boolean hasMod(String mod) {
		return FabricLoader.getInstance().isModLoaded(mod);
	}
}
