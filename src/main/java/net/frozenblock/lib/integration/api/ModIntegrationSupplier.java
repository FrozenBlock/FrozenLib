/*
 * Copyright 2023 FrozenBlock
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
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
