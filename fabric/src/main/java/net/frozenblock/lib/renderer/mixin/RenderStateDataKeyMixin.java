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

package net.frozenblock.lib.renderer.mixin;

import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.frozenblock.lib.renderer.FrozenLibRenderState;
import net.frozenblock.lib.renderer.RenderStateDataKey;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@ClientOnly
@Mixin(FabricRenderState.class)
public interface RenderStateDataKeyMixin extends FrozenLibRenderState { // in common mixins.json

	@Shadow
	@Nullable <T> T getData(net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey<T> key);

	@Shadow
	<T> T getDataOrDefault(net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey<T> key, T defaultValue);

	@Shadow
	<T> void setData(net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey<T> key, @Nullable T value);

	@Shadow
	void clearExtraData();

	@Unique
	@Override
	default <T> T frozenLib$getData(RenderStateDataKey<T> key) {
		return this.getData(getOrCreateFabricKey(key));
	}

	@Unique
	@Override
	default <T> T frozenLib$getDataOrDefault(RenderStateDataKey<T> key, T defaultValue) {
		return this.getDataOrDefault(getOrCreateFabricKey(key), defaultValue);
	}

	@Unique
	@Override
	default <T> void frozenLib$setData(RenderStateDataKey<T> key, @Nullable T value) {
		this.setData(getOrCreateFabricKey(key), value);
	}

	@Unique
	@Override
	default void frozenLib$clearExtraData() {
		this.clearExtraData();
	}

	@Unique
	private static <T>net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey<T> getOrCreateFabricKey(RenderStateDataKey<T> key) {
		final net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey<T> fabricKey = (net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey<T>) key.getFabricKey();
		if (fabricKey != null) return fabricKey;

		key.setFabricKey(net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey.create(() -> key.asContextKey().name().toString()));
		return (net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey<T>) key.getFabricKey();
	}
}
