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

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Map;
import net.frozenblock.lib.renderer.FrozenLibRenderState;
import net.frozenblock.lib.renderer.RenderStateDataKey;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//TODO: see if fabric ever implements this themselves
@ClientOnly
@Mixin(PlayerRenderState.class)
public class PlayerRenderStateDataKeyMixin implements FrozenLibRenderState {

	@Unique
	@Nullable
	private Map<RenderStateDataKey<?>, Object> frozenLib$renderStateData;

	@Inject(method = "reset", at = @At("HEAD"))
	public void frozenLib$reset(CallbackInfo info) {
		this.frozenLib$clearExtraData();
	}

	@Unique
	@Override
	public <T> T frozenLib$getData(RenderStateDataKey<T> key) {
		return this.frozenLib$renderStateData == null ? null : (T) this.frozenLib$renderStateData.get(key);
	}

	@Unique
	@Override
	public <T> T frozenLib$getDataOrDefault(RenderStateDataKey<T> key, T defaultValue) {
		return this.frozenLib$renderStateData == null ? defaultValue : (T) this.frozenLib$renderStateData.getOrDefault(key, defaultValue);
	}

	@Unique
	@Override
	public <T> void frozenLib$setData(RenderStateDataKey<T> key, @Nullable T value) {
		if (this.frozenLib$renderStateData == null) this.frozenLib$renderStateData = new Reference2ObjectOpenHashMap<>();
		this.frozenLib$renderStateData.put(key, value);
	}

	@Unique
	@Override
	public void frozenLib$clearExtraData() {
		if (this.frozenLib$renderStateData != null) this.frozenLib$renderStateData.clear();
	}
}
