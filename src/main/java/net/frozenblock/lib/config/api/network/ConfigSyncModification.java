/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.config.api.network;

import java.util.function.Consumer;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.api.instance.ConfigModification;

/**
 * @since 1.4.5
 */
public record ConfigSyncModification<T>(Config<T> config, DataSupplier<T> dataSupplier) implements Consumer<T> {

	@Override
	public void accept(T destination) {
		T source = dataSupplier.get(config).instance();
		ConfigModification.copyInto(source, destination);
	}

	@FunctionalInterface
	public interface DataSupplier<T> {
		ConfigSyncData<T> get(Config<T> config);
	}
}
