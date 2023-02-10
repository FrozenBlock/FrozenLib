package net.frozenblock.lib.config.impl.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.config.api.client.ClientConfig;
import net.frozenblock.lib.config.api.client.FrozenConfigScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.function.Consumer;

@ApiStatus.Internal
@Environment(EnvType.CLIENT)
public class ClientConfigImpl implements ClientConfig {

	private final Component title;

	private final Runnable onSave;
	private final Consumer<FrozenConfigScreen> initializer;

	public ClientConfigImpl(Component title, Runnable onSave, Consumer<FrozenConfigScreen> initializer) {
		this.title = title;
		this.onSave = onSave;
		this.initializer = initializer;
	}

	@Override
	public Component title() {
		return this.title;
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
		private Runnable onSave = () -> {};
		private Consumer<FrozenConfigScreen> initializer = screen -> {};

		@Override
		public Builder title(@NotNull Component title) {
			this.title = title;
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
			return new ClientConfigImpl(this.title, this.onSave, this.initializer);
		}
	}
}
