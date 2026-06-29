package net.frozenblock.lib.renderer.mixin;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.frozenblock.lib.renderer.FrozenLibRenderState;
import net.frozenblock.lib.renderer.RenderStateDataKey;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Environment(EnvType.CLIENT)
@Mixin(FabricRenderState.class)
public interface RenderStateDataKeyMixin extends FrozenLibRenderState {

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
	public default <T> T getData(RenderStateDataKey<T> key) {
		return this.getData(getOrCreateFabricKey(key));
	}

	@Unique
	@Override
	public default <T> T getDataOrDefault(RenderStateDataKey<T> key, T defaultValue) {
		return this.getDataOrDefault(getOrCreateFabricKey(key), defaultValue);
	}

	@Unique
	@Override
	public default <T> void setData(RenderStateDataKey<T> key, @Nullable T value) {
		this.setData(getOrCreateFabricKey(key), value);
	}

	@Unique
	@Override
	public default void frozenLib$clearExtraData() {
		this.clearExtraData();
	}

	@Unique
	private static <T>net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey<T> getOrCreateFabricKey(RenderStateDataKey<T> key) {
		final net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey<T> fabricKey = (net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey<T>) key.getFabricKey();
		if (fabricKey != null) return fabricKey;

		key.setFabricKey(RenderStateDataKey.create(key.asContextKey().name()));
		return (net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey<T>) key.getFabricKey();
	}
}
