/*
 * Copyright (C) 2024-2025 FrozenBlock
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

package net.frozenblock.lib.render.api;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.frozenblock.lib.entrypoint.api.ClientEventEntrypoint;
import net.frozenblock.lib.event.api.FrozenEvents;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class ShaderRegistryAPI {

	/**
	 * An event that triggers while Minecraft's shaders are being loaded.
	 */
	public static final Event<ShadersLoading> SHADERS_LOADING = FrozenEvents.createEnvironmentEvent(
		ShadersLoading.class,
		callbacks -> (resourceProvider, shaderList) -> {
			for (var callback : callbacks) {
				callback.onShadersLoading(resourceProvider, shaderList);
			}
		});

	public static void registerShader(
		@NotNull List<Pair<ShaderInstance, Consumer<ShaderInstance>>> shaderList,
		ShaderInstance shaderInstance,
		Consumer<ShaderInstance> onLoaded
	) {
		shaderList.add(Pair.of(shaderInstance, onLoaded));
	}

	@FunctionalInterface
	public interface ShadersLoading extends ClientEventEntrypoint {
		void onShadersLoading(ResourceProvider resourceProvider, List<Pair<ShaderInstance, Consumer<ShaderInstance>>> shaderList) throws IOException;
	}

}
