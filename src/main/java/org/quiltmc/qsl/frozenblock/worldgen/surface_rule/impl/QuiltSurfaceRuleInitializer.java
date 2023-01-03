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

package org.quiltmc.qsl.frozenblock.worldgen.surface_rule.impl;

import net.minecraft.core.registries.Registries;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.qsl.frozenblock.core.registry.api.event.DynamicRegistryManagerSetupContext;
import org.quiltmc.qsl.frozenblock.core.registry.api.event.RegistryEntryContext;
import org.quiltmc.qsl.frozenblock.core.registry.api.event.RegistryEvents;
import org.quiltmc.qsl.frozenblock.worldgen.surface_rule.api.SurfaceRuleEvents;
import org.quiltmc.qsl.frozenblock.worldgen.surface_rule.mixin.NoiseGeneratorSettingsAccessor;

/**
 * Modified to work on Fabric
 */
@ApiStatus.Internal
public class QuiltSurfaceRuleInitializer implements RegistryEvents.DynamicRegistrySetupCallback {

	@Override
	public void onDynamicRegistrySetup(@NotNull DynamicRegistryManagerSetupContext context) {
		context.monitor(Registries.NOISE_SETTINGS, monitor -> {
			monitor.forAll(ctx -> this.modifyChunkGeneratorSettings(ctx, context.resourceManager()));
		});
	}

	private void modifyChunkGeneratorSettings(RegistryEntryContext<NoiseGeneratorSettings> context, ResourceManager resourceManager) {
		var baseSurfaceRule = context.value().surfaceRule();

		SurfaceRuleContextImpl globalImpl;
		if (context.resourceLocation().equals(NoiseGeneratorSettings.OVERWORLD.location())
				|| context.resourceLocation().equals(NoiseGeneratorSettings.AMPLIFIED.location())
				|| context.resourceLocation().equals(NoiseGeneratorSettings.LARGE_BIOMES.location())) {
			globalImpl = this.modifyOverworld(true, false, true,
					baseSurfaceRule, resourceManager, context);
		} else if (context.resourceLocation().equals(NoiseGeneratorSettings.CAVES.location())) {
			globalImpl = this.modifyOverworld(false, true, true,
					baseSurfaceRule, resourceManager, context);
		} else if (context.resourceLocation().equals(NoiseGeneratorSettings.FLOATING_ISLANDS.location())) {
			globalImpl = this.modifyOverworld(false, false, false,
					baseSurfaceRule, resourceManager, context);
		} else if (context.resourceLocation().equals(NoiseGeneratorSettings.NETHER.location())) {
			var impl = new SurfaceRuleContextImpl.NetherImpl(baseSurfaceRule, resourceManager, context.resourceLocation());
			SurfaceRuleEvents.MODIFY_NETHER.invoker().modifyNetherRules(impl);
			globalImpl = impl;
		} else if (context.resourceLocation().equals(NoiseGeneratorSettings.END.location())) {
			var impl = new SurfaceRuleContextImpl.TheEndImpl(baseSurfaceRule, resourceManager, context.resourceLocation());
			SurfaceRuleEvents.MODIFY_THE_END.invoker().modifyTheEndRules(impl);
			globalImpl = impl;
		} else {
			globalImpl = new SurfaceRuleContextImpl(baseSurfaceRule, resourceManager, context.resourceLocation());
			SurfaceRuleEvents.MODIFY_GENERIC.invoker().modifyGenericSurfaceRules(globalImpl);
		}

		((NoiseGeneratorSettingsAccessor) (Object) context.value()).setSurfaceRule(globalImpl.freeze());
	}

	private SurfaceRuleContextImpl modifyOverworld(boolean surface, boolean bedrockRoof, boolean bedrockFloor,
												   SurfaceRules.RuleSource baseSurfaceRule, ResourceManager resourceManager, RegistryEntryContext<NoiseGeneratorSettings> context) {
		var impl = new SurfaceRuleContextImpl.OverworldImpl(surface, bedrockRoof, bedrockFloor,
				baseSurfaceRule, resourceManager, context.resourceLocation());
		SurfaceRuleEvents.MODIFY_OVERWORLD.invoker().modifyOverworldRules(impl);
		return impl;
	}
}
