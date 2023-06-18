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

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.config.api.client.option.Option;
import net.frozenblock.lib.config.api.client.option.OptionType;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public interface ClientConfig {

	Component title();

	List<Option<?>> options();

	Runnable save();

	Consumer<FrozenConfigScreen> init();

	@Environment(EnvType.CLIENT)
	Screen makeScreen(@Nullable Screen parent);
/*
	static Builder makeBuilder() {
		return new ClientConfigImpl.BuilderImpl();
	}

	static <T> ClientConfig make(Config<T> config, ConfigBuilder<T> builder) {
		return builder.build(config.defaultInstance(), config.config(), makeBuilder().save(config::save)).build();
	}
*/
	interface Builder {

		Builder title(@NotNull Component title);

		<T> Builder option(@NotNull T value, @NotNull OptionType type, @NotNull Component text, @NotNull Option.Save<T> onSave);

		<T> Builder option(@NotNull T value, @NotNull OptionType type, @NotNull Component text, @NotNull Optional<Component> tooltip, @NotNull Option.Save<T> onSave);

		Builder save(@NotNull Runnable onSave);

		Builder init(@NotNull Consumer<FrozenConfigScreen> initializer);

		ClientConfig build();
	}
}
