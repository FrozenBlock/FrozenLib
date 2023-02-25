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

package net.frozenblock.lib.config.api.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.config.api.client.option.Option;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.impl.client.ClientConfigImpl;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public interface ClientConfig<T> {

	Config<T> config();

	Component title();

	List<Option<?>> options();

	Runnable save();

	Consumer<FrozenConfigScreen> init();

	@Environment(EnvType.CLIENT)
	Screen makeScreen(@Nullable Screen parent);

	static <T> Builder<T> makeBuilder() {
		return new ClientConfigImpl.BuilderImpl<>();
	}

	@SuppressWarnings("unchecked")
	static <T> ClientConfig<T> make(Config<T> config, ConfigBuilder<T> builder) {
		return builder.build(config, config.defaultInstance(), config.config(), (Builder<T>) makeBuilder().save(config::save)).build();
	}

	interface Builder<T> {

		Builder<T> config(@NotNull Config<T> config);

		Builder<T> title(@NotNull Component title);

		<O> Builder<T> option(@NotNull Option<O> option);

		Builder<T> save(@NotNull Runnable onSave);

		Builder<T> init(@NotNull Consumer<FrozenConfigScreen> initializer);

		ClientConfig<T> build();
	}
}
