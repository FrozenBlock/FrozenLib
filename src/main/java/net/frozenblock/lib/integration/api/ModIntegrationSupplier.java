/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.integration.api;

import java.util.Optional;
import java.util.function.Supplier;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.integration.impl.EmptyModIntegration;

public class ModIntegrationSupplier<T extends ModIntegration> {
	protected final String modID;
	protected final boolean isModLoaded;
	protected final Optional<T> optionalIntegration;
	protected final T unloadedModIntegration;

	public ModIntegrationSupplier(Supplier<T> modIntegrationSupplier, String modID) {
		this.modID = modID;
		this.isModLoaded = FabricLoader.getInstance().isModLoaded(this.modID);
		this.optionalIntegration = this.modLoaded() ? Optional.of(modIntegrationSupplier.get()) : Optional.empty();
		this.unloadedModIntegration = (T) new EmptyModIntegration(modID);
	}

	public ModIntegrationSupplier(Supplier<T> modIntegrationSupplier, Supplier<T> unloadedModIntegrationSupplier, String modID) {
		this.modID = modID;
		this.isModLoaded = FabricLoader.getInstance().isModLoaded(this.modID);
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
		return isModLoaded;
	}
}
