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
import net.frozenblock.lib.event.api.events.ClientConnectionEvents;
import net.frozenblock.lib.event.api.events.ClientLevelEvents;
import net.frozenblock.lib.event.api.events.ClientTickEvents;
import net.frozenblock.lib.particle.client.resource.FrozenLibParticleResources;
import net.frozenblock.lib.platform.api.client.hud.FrozenHudElements;
import net.frozenblock.lib.platform.api.client.hud.VanillaHudAnchor;
import net.frozenblock.lib.registry.client.FrozenLibClientRegistries;
import net.frozenblock.lib.renderer.model.FrozenLibModelLayers;
import net.frozenblock.lib.resource_pack.api.client.FrozenLibModResourcePackApi;
import net.frozenblock.lib.screenshake.api.client.ClientScreenShaker;
import net.frozenblock.lib.sound.client.impl.FlyBySoundHub;
import net.frozenblock.lib.wind.WindManager;
import net.frozenblock.lib.wind.client.ClientWindUtil;
import net.minecraft.client.Minecraft;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.client.ClientRegistrySync;
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.impl.client.ClientFreezer;

@Environment(EnvType.CLIENT)
public final class FrozenLibClient {

	public static void preQuiltInit() {
		FrozenLibClientRegistries.init();
	}

	public static void quiltSetup() {
		ClientFreezer.onSetupClient();
		ClientRegistrySync.registerHandlers();
	}

	public static void init() {
		ClientCapeUtil.init();
		FrozenLibParticleResources.init();
		FrozenLibModelLayers.init();
		FrozenLibModResourcePackApi.init();
		ClientWindUtil.init();

		FrozenHudElements.attachElementAfter(
			VanillaHudAnchor.MISC_OVERLAYS,
			FrozenLibConstants.id("spotting_icons"),
			new SpottingIconHudElement()
		);

		ClientTickEvents.START_LEVEL_TICK.register(
			level -> {
				final Minecraft minecraft = Minecraft.getInstance();
				WindManager.getOrCreate(level).tick(level);
				ClientScreenShaker.tick(minecraft, level);
				FlyBySoundHub.tick(minecraft, minecraft.getCameraEntity(), true);
			}
		);

		ClientConnectionEvents.DISCONNECT.register((handler, client) -> {
			clearStaticClientData();
		});

		ClientLevelEvents.AFTER_CLIENT_LEVEL_CHANGE.register((client, level) -> {
			clearStaticClientData();
		});
	}

	private static void clearStaticClientData() {
		ClientScreenShaker.reset();
	}
}
