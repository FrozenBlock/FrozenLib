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

package net.frozenblock.lib.config.impl.client;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.config.api.client.ClientConfig;
import net.frozenblock.lib.config.api.client.FrozenConfigScreen;
import net.frozenblock.lib.config.api.client.option.Option;
import net.frozenblock.lib.config.api.instance.Config;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

@ApiStatus.Internal
@Environment(EnvType.CLIENT)
public class ClientConfigImpl<T> implements ClientConfig<T> {

	private final Config<T> config;
	private final Component title;

	private final ImmutableList<Option<?>> options;

	private final Runnable onSave;
	private final Consumer<FrozenConfigScreen> initializer;

	public ClientConfigImpl(Config<T> config, Component title, Collection<Option<?>> options, Runnable onSave, Consumer<FrozenConfigScreen> initializer) {
		assert config != null : "Config cannot be null";
		this.config = config;
		this.title = title;
		this.options = ImmutableList.copyOf(options);
		this.onSave = onSave;
		this.initializer = initializer;
	}

	@Override
	public Config<T> config() {
		return this.config;
	}

	@Override
	public Component title() {
		return this.title;
	}

	@Override
	public List<Option<?>> options() {
		return List.copyOf(this.options);
	}

	@Override
	public Runnable save() {
		FrozenMain.log("Client config saving", FrozenMain.UNSTABLE_LOGGING);
		return this.onSave;
	}

	@Override
	public Consumer<FrozenConfigScreen> init() {
		return this.initializer;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public Screen makeScreen(@Nullable Screen parent) {
		return new FrozenConfigScreen(this, parent);
	}

	@ApiStatus.Internal
	public static final class BuilderImpl<T> implements ClientConfig.Builder<T> {
		private Config<T> config;
		private Component title;
		private final List<Option<?>> options = new ArrayList<>();
		private Runnable onSave = () -> {};
		private Consumer<FrozenConfigScreen> initializer = screen -> {};

		public BuilderImpl(Config<T> config) {
			this.config = config;
		}

		@Override
		public Builder<T> title(@NotNull Component title) {
			this.title = title;
			return this;
		}

		@Override
		public <O> Builder<T> option(@NotNull Option<O> option) {
			this.options.add(option);
			return this;
		}

		@Override
		public Builder<T> save(@NotNull Runnable onSave) {
			this.onSave = onSave;
			return this;
		}

		@Override
		public Builder<T> init(@NotNull Consumer<FrozenConfigScreen> initializer) {
			this.initializer = initializer;
			return this;
		}

		@Override
		public ClientConfig<T> build() {
			return new ClientConfigImpl<>(this.config, this.title, this.options, this.onSave, this.initializer);
		}
	}
}
