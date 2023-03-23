package net.frozenblock.lib.entrypoint.api;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public abstract class FrozenModInitializer implements ModInitializer {

	private final String modId;

	public FrozenModInitializer(String modId) {
		this.modId = modId;
	}

	@Override
	public void onInitialize() {
		this.onInitialize(this.modId);
	}

	public abstract void onInitialize(String modId);

	public String modId() {
		return this.modId;
	}

	public ResourceLocation id(String path) {
		return new ResourceLocation(this.modId, path);
	}

	public <T> T register(Registry<T> registry, String path, T value) {
		return Registry.register(registry, this.id(path), value);
	}
}
