package net.frozenblock.lib.integration.impl;

import java.util.Optional;
import java.util.function.Supplier;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.integration.api.ModIntegration;
import net.frozenblock.lib.integration.api.UnloadedModIntegration;

public class ModIntegrationSupplier<T extends ModIntegration> {
	private final String modID;
	private final Optional<T> optionalIntegration;
	private final ModIntegration unloadedModIntegration;

	public ModIntegrationSupplier(Supplier<T> modIntegrationSupplier, String modID) {
		this.modID = modID;
		this.optionalIntegration = this.modLoaded() ? Optional.of(modIntegrationSupplier.get()) : Optional.empty();
		this.unloadedModIntegration = new UnloadedModIntegration(modID);
	}

	public ModIntegration getIntegration() {
		return this.optionalIntegration.isPresent() ? this.optionalIntegration.get() : this.unloadedModIntegration;
	}

	public Optional<T> get() {
		return this.optionalIntegration;
	}

	public boolean modLoaded() {
		return FabricLoader.getInstance().isModLoaded(this.modID);
	}
}
