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
			SurfaceRules.RuleSource overworldSource = FrozenSurfaceRules.sequence(overworldRules);
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
