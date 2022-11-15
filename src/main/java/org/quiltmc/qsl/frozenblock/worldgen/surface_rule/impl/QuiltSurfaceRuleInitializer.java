/*
 * Copyright 2022 QuiltMC
 * Copyright 2022 FrozenBlock
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

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.qsl.frozenblock.core.registry.api.event.DynamicRegistryManagerSetupContext;
import org.quiltmc.qsl.frozenblock.core.registry.api.event.RegistryEntryContext;
import org.quiltmc.qsl.frozenblock.core.registry.api.event.RegistryEvents;
import org.quiltmc.qsl.frozenblock.core.registry.api.event.RegistryMonitor;
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
			monitor.forAll((registry, ctx) -> this.modifyChunkGeneratorSettings(ctx, context.resourceManager()));
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
