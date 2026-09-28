/*
 * Copyright (C) 2024-2026 FrozenBlock
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
import net.frozenblock.lib.renderer.FrozenLibRenderState;
import net.frozenblock.lib.renderer.RenderStateDataKey;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.GameRenderState;
import net.minecraft.client.renderer.state.LightmapRenderState;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.client.renderer.state.OptionsRenderState;
import net.minecraft.client.renderer.state.WindowRenderState;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.client.renderer.state.gui.PanoramaRenderState;
import net.minecraft.client.renderer.state.level.BlockBreakingRenderState;
import net.minecraft.client.renderer.state.level.BlockOutlineRenderState;
import net.minecraft.client.renderer.state.level.CameraEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.client.renderer.state.level.ParticlesRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.client.renderer.state.level.SkyRenderState;
import net.minecraft.client.renderer.state.level.WeatherRenderState;
import net.minecraft.client.renderer.state.level.WorldBorderRenderState;
import net.minecraft.util.context.ContextKey;
import net.neoforged.neoforge.client.extensions.IRenderStateExtension;
import net.neoforged.neoforge.client.renderstate.BaseRenderState;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import java.util.Map;

@ClientOnly
@Mixin({
	BlockModelRenderState.class,
	MovingBlockRenderState.class,
	BlockEntityRenderState.class,
	EntityRenderState.class,
	EntityRenderState.LeashState.class,
	FirstPersonHandsAndItemsRenderState.class,
	FogData.class,
	ItemStackRenderState.class,
	ItemStackRenderState.LayerRenderState.class,
	GameRenderState.class,
	LightmapRenderState.class,
	MapRenderState.class,
	MapRenderState.MapDecorationRenderState.class,
	OptionsRenderState.class,
	WindowRenderState.class,
	GuiRenderState.class,
	PanoramaRenderState.class,
	BlockBreakingRenderState.class,
	BlockOutlineRenderState.class,
	CameraEntityRenderState.class,
	CameraRenderState.class,
	LevelRenderState.class,
	ParticlesRenderState.class,
	PlayerRenderState.class,
	PlayerRenderState.ItemActivationRenderState.class,
	SkyRenderState.class,
	WeatherRenderState.class,
	WorldBorderRenderState.class
})
public abstract class FrozenLibRenderStateMixin implements FrozenLibRenderState { // In common mixins.json
	@Unique
	private final Map<ContextKey<?>, Object> frozenLib$extraData = new Reference2ObjectOpenHashMap<>();

	@Unique
	@Override
	@SuppressWarnings("unchecked")
	public <T> T frozenLib$getData(RenderStateDataKey<T> key) {
		if (this instanceof IRenderStateExtension extension) return extension.getRenderData(key.asContextKey());
		return (T) this.frozenLib$extraData.get(key.asContextKey());
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
		if (this instanceof IRenderStateExtension extension) {
			extension.setRenderData(key.asContextKey(), value);
			return;
		}

		if (value != null) {
			this.frozenLib$extraData.put(key.asContextKey(), value);
		} else {
			this.frozenLib$extraData.remove(key.asContextKey());
		}
	}

	@Unique
	@Override
	public void frozenLib$clearExtraData() {
		if ((Object) this instanceof BaseRenderState baseRenderState) {
			baseRenderState.resetRenderData();
			return;
		}

		this.frozenLib$extraData.clear();
	}
}
