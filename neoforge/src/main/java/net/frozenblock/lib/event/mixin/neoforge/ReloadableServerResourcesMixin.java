/*
 * Copyright (C) 2026 FrozenBlock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.event.mixin.neoforge;

/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
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

import net.frozenblock.lib.event.api.events.CommonLifecycleEvents;
import net.minecraft.commands.Commands;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.world.flag.FeatureFlagSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;

@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin {
	@Unique
	private RegistryAccess frozenLib$fabric$layeredRegistries;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void frozenLib$fabric$init(
		LayeredRegistryAccess<RegistryLayer> fullLayers,
		HolderLookup.Provider loadingContext,
		FeatureFlagSet enabledFeatures,
		Commands.CommandSelection commandSelection,
		List postponedTags,
		PermissionSet functionCompilationPermissions,
		List newComponents,
		CallbackInfo info
	) {
		this.frozenLib$fabric$layeredRegistries = fullLayers.compositeAccess();
	}

	/**
	 * @reason NeoForge's event is called before pending Data Components are applied. This mixin maintains parity with Fabric.
	 */
	@Inject(method = "updateComponentsAndStaticRegistryTags", at = @At("TAIL"))
	private void frozenLib$fabric$hookRefresh(CallbackInfo info) {
		CommonLifecycleEvents.TAGS_LOADED.invoker().onTagsLoaded(this.frozenLib$fabric$layeredRegistries, false);
	}
}
