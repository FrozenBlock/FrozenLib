/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.terrablender;

import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.worldgen.surface.FrozenSurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules;
import terrablender.api.SurfaceRuleManager;
import terrablender.api.TerraBlenderApi;
import java.util.ArrayList;

public class FrozenTerraBlenderCompat implements TerraBlenderApi {

	@Override
	public void onTerraBlenderInitialized() {
		ArrayList<SurfaceRules.RuleSource> overworldRules = new ArrayList<>();
		FrozenMain.SURFACE_RULE_ENTRYPOINTS.forEach((entrypoint -> entrypoint.getEntrypoint().addOverworldSurfaceRules(overworldRules)));
		if (!overworldRules.isEmpty()) {
			SurfaceRules.RuleSource overworldSource = SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(), FrozenSurfaceRules.sequence(overworldRules));
			SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, "frozenlib_terrablender_compat_overworld", overworldSource);
		}

		ArrayList<SurfaceRules.RuleSource> netherRules = new ArrayList<>();
		FrozenMain.SURFACE_RULE_ENTRYPOINTS.forEach((entrypoint -> entrypoint.getEntrypoint().addNetherSurfaceRules(netherRules)));
		if (!overworldRules.isEmpty()) {
			SurfaceRules.RuleSource netherSource = FrozenSurfaceRules.sequence(netherRules);
			SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.NETHER, "frozenlib_terrablender_compat_nether", netherSource);
		}
	}

}
