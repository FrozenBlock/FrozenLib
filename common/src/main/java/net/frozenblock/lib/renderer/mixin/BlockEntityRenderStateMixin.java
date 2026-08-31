package net.frozenblock.lib.renderer.mixin;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Map;
import net.frozenblock.lib.renderer.FrozenLibRenderState;
import net.frozenblock.lib.renderer.RenderStateDataKey;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.util.context.ContextKey;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@ClientOnly
@Mixin(BlockEntityRenderState.class)
public abstract class BlockEntityRenderStateMixin implements FrozenLibRenderState {

	@Unique
	private final Map<ContextKey<?>, Object> frozenLib$extraData = new Reference2ObjectOpenHashMap<>();

	@Unique
	@Override
	@SuppressWarnings("unchecked")
	public <T> T frozenLib$getData(RenderStateDataKey<T> key) {
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
		if (value != null) {
			this.frozenLib$extraData.put(key.asContextKey(), value);
		} else {
			this.frozenLib$extraData.remove(key.asContextKey());
		}
	}

	@Unique
	@Override
	public void frozenLib$clearExtraData() {
		this.frozenLib$extraData.clear();
	}
}
