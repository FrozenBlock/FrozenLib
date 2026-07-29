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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.Map;
import java.util.stream.Collectors;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.renderer.FrozenLibRenderState;
import net.frozenblock.lib.renderer.RenderStateDataKey;
import net.frozenblock.lib.platform.api.ClientOnly;
import net.minecraft.util.context.ContextKey;
import net.neoforged.neoforge.client.renderstate.BaseRenderState;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@ClientOnly
@Mixin(BaseRenderState.class)
public abstract class RenderStateDataKeyMixin implements FrozenLibRenderState {

	@Shadow
	public abstract <T> T getRenderData(ContextKey<T> key);

	@Shadow
	public abstract <T> void setRenderData(ContextKey<T> key, @Nullable T data);

	@Shadow
	public abstract void resetRenderData();

	@WrapOperation(
		method = "resetRenderData",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/Map;clear()V"
		)
	)
	private void frozenLib$keepFrozenLibData(Map<ContextKey<?>, Object> extensions, Operation<Void> original) {
		final var frozenlibData = extensions.entrySet()
			.stream().filter(key -> key.getKey().name().getNamespace().equals(FrozenLibConstants.MOD_ID))
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		original.call(extensions);
		extensions.putAll(frozenlibData);
	}

	@Unique
	@Override
	public <T> T frozenLib$getData(RenderStateDataKey<T> key) {
		return this.getRenderData(key.asContextKey());
	}

	@Unique
	@Override
	public <T> T frozenLib$getDataOrDefault(RenderStateDataKey<T> key, T defaultValue) {
		final T value = this.frozenLib$getData(key);
		return value != null ? value : defaultValue;
	}

	@Unique
	@Override
	public <T> void frozenLib$setData(RenderStateDataKey<T> key, @Nullable T value) {
		this.setRenderData(key.asContextKey(), value);
	}

	@Unique
	@Override
	public void frozenLib$clearExtraData() {
		this.resetRenderData();
	}
}
