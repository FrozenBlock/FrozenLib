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

package net.frozenblock.lib.renderer;

/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
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
 */

import net.frozenblock.lib.platform.api.ClientOnly;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

@ApiStatus.NonExtendable
@ClientOnly
public interface FrozenLibRenderState {
	/**
	 * Get extra render data from the render state.
	 * @param key the key of the data
	 * @param <T> the type of the data
	 * @return the data, or {@code null} if it cannot be found.
	 */
	@Nullable
	default <T> T frozenLib$getData(RenderStateDataKey<T> key) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	/**
	 * Get extra render data from the render state, or a default value if it cannot be found.
	 * @param key the key of the data
	 * @param defaultValue the default value
	 * @param <T> the type of the data
	 * @return the data, or the default value if it cannot be found.
	 */
	default <T> T frozenLib$getDataOrDefault(RenderStateDataKey<T> key, T defaultValue) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	/**
	 * Set extra render data to the render state.
	 * @param key the key of the data
	 * @param value the data
	 * @param <T> the type of the data
	 */
	default <T> void frozenLib$setData(RenderStateDataKey<T> key, @Nullable T value) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	/**
	 * Clears all extra render data on the render state.
	 */
	default void frozenLib$clearExtraData() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}
}
