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

package net.frozenblock.lib;

import net.fabricmc.api.EnvType;import net.fabricmc.api.Environment;import net.frozenblock.lib.cape.client.api.ClientCapeUtil;
import net.frozenblock.lib.entity.client.impl.spottingicon.SpottingIconHudElement;
import net.frozenblock.lib.particle.client.resource.FrozenLibParticleResources;
import net.frozenblock.lib.platform.api.client.hud.FrozenHudElements;
import net.frozenblock.lib.platform.api.client.hud.VanillaHudAnchor;
import net.frozenblock.lib.registry.client.FrozenLibClientRegistries;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.client.ClientRegistrySync;
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.impl.client.ClientFreezer;

@Environment(EnvType.CLIENT)
public final class FrozenLibClient {

	public static void preQuiltInit() {
		FrozenLibClientRegistries.init();
	}

	public static void quiltInit() {
		ClientFreezer.onInitializeClient();
		ClientRegistrySync.registerHandlers();
	}

	public static void init() {
		ClientCapeUtil.init();
		FrozenLibParticleResources.init();

		FrozenHudElements.attachElementAfter(
			VanillaHudAnchor.MISC_OVERLAYS,
			FrozenLibConstants.id("spotting_icons"),
			new SpottingIconHudElement()
		);
	}
}
