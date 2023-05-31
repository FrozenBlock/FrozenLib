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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.config.api.client.ClientConfig;
import net.frozenblock.lib.config.api.client.FrozenConfigScreen;
import net.frozenblock.lib.config.api.client.option.Option;
import net.frozenblock.lib.config.api.client.option.OptionType;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
@Environment(EnvType.CLIENT)
public class ClientConfigImpl implements ClientConfig {

	private final Component title;

	private final ImmutableList<Option<?>> options;

	private final Runnable onSave;
	private final Consumer<FrozenConfigScreen> initializer;

	public ClientConfigImpl(Component title, Collection<Option<?>> options, Runnable onSave, Consumer<FrozenConfigScreen> initializer) {
		this.title = title;
		this.options = ImmutableList.copyOf(options);
		this.onSave = onSave;
		this.initializer = initializer;
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
	public static final class BuilderImpl implements ClientConfig.Builder {
		private Component title;
		private List<Option<?>> options = new ArrayList<>();
		private Runnable onSave = () -> {};
		private Consumer<FrozenConfigScreen> initializer = screen -> {};

		@Override
		public Builder title(@NotNull Component title) {
			this.title = title;
			return this;
		}

		@Override
		public <T> Builder option(@NotNull T value, @NotNull OptionType type, @NotNull Component text, @NotNull Option.Save<T> onSave) {
			return option(value, type, text, Optional.empty(), onSave);
		}

		@Override
		public <T> Builder option(@NotNull T value, @NotNull OptionType type, @NotNull Component text, @NotNull Optional<Component> tooltip, @NotNull Option.Save<T> onSave) {
			this.options.add(new Option<>(value, type, text, tooltip, onSave));
			return this;
		}

		@Override
		public Builder save(@NotNull Runnable onSave) {
			this.onSave = onSave;
			return this;
		}

		@Override
		public Builder init(@NotNull Consumer<FrozenConfigScreen> initializer) {
			this.initializer = initializer;
			return this;
		}

		@Override
		public ClientConfig build() {
			return new ClientConfigImpl(this.title, this.options, this.onSave, this.initializer);
		}
	}
}
