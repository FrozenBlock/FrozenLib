package net.frozenblock.lib.config.api.client.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public record Option<T>(OptionType type, Component text, Optional<Component> tooltip, Runnable onSave) {

	public interface Save<T> {
		void save(T data);
	}
}
