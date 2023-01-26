package net.frozenblock.lib.integration.api;

import java.util.Optional;
import java.util.function.Supplier;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.integration.impl.EmptyModIntegration;

public class ModIntegrationSupplier<T extends ModIntegration> {
	private final String modID;
	private final Optional<T> optionalIntegration;
	private final T unloadedModIntegration;

	public ModIntegrationSupplier(Supplier<T> modIntegrationSupplier, String modID) {
		this.modID = modID;
		this.optionalIntegration = this.modLoaded() ? Optional.of(modIntegrationSupplier.get()) : Optional.empty();
		this.unloadedModIntegration = (T) new EmptyModIntegration(modID);
	}

	public ModIntegrationSupplier(Supplier<T> modIntegrationSupplier, Supplier<T> unloadedModIntegrationSupplier, String modID) {
		this.modID = modID;
		this.optionalIntegration = this.modLoaded() ? Optional.of(modIntegrationSupplier.get()) : Optional.empty();
		this.unloadedModIntegration = unloadedModIntegrationSupplier.get();
	}

	public T getIntegration() {
		return this.optionalIntegration.orElse(this.unloadedModIntegration);
	}

	public Optional<T> get() {
		return this.optionalIntegration;
	}

	public boolean modLoaded() {
		return FabricLoader.getInstance().isModLoaded(this.modID);
	}
}
