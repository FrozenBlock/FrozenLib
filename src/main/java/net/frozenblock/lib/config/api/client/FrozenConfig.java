package net.frozenblock.lib.config.api.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.config.api.ConfigBuilder;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.impl.FrozenConfigImpl;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public interface FrozenConfig {

	Component title();

	Runnable save();

	Consumer<FrozenConfigScreen> init();

	@Environment(EnvType.CLIENT)
	Screen makeScreen(@Nullable Screen parent);

	static Builder makeBuilder() {
		return new FrozenConfigImpl.BuilderImpl();
	}

	static <T> FrozenConfig make(Config<T> config, ConfigBuilder<T> builder) {
		return builder.build(config.defaultInstance(), config.config(), makeBuilder().save(config::save)).build();
	}

	interface Builder {

		Builder title(@NotNull Component title);

		Builder save(@NotNull Runnable onSave);

		Builder init(@NotNull Consumer<FrozenConfigScreen> initializer);

		FrozenConfig build();
	}
}
