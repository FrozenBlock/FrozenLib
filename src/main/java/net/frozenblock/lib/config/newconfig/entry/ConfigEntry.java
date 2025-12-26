/*
 * Copyright (C) 2025 FrozenBlock
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

package net.frozenblock.lib.config.newconfig.entry;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.frozenblock.lib.config.newconfig.ConfigSerializer;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public class ConfigEntry<T> {
	private final Identifier id;
	private final EntryType<T> type;
	private final T defaultValue;
	// TODO: Figure out how to save comments!!
	private final Optional<String> comment;

	private T value;
	private Optional<T> syncedValue = Optional.empty();
	private boolean dirty;
	private boolean hasCheckedLoad;

	public ConfigEntry(Identifier id, EntryType<T> type, T defaultValue, Optional<String> comment) {
		this.id = id;
		this.type = type;
		this.defaultValue = defaultValue;
		this.value = defaultValue;
		this.comment = comment;
		Registry.register(FrozenLibRegistries.CONFIG_ENTRY, id, this);
	}

	public ConfigEntry(Identifier id, EntryType<T> type, T defaultValue) {
		this(id, type, defaultValue, Optional.empty());
	}

	public T get() {
		return this.syncedValue.orElseGet(this::getActual);
	}

	public T getActual() {
		this.ensureIsLoaded();
		return this.value;
	}

	public void setValue(T value) {
		this.setValue(value, true);
	}

	public void setValue(T value, boolean markDirty) {
		this.ensureIsLoaded();
		this.value = value;
		if (markDirty) this.markDirty();
	}

	public void ensureIsLoaded() {
		if (this.hasCheckedLoad) return;
		this.hasCheckedLoad = true;
		ConfigSerializer.loadConfigFromEntry(this);
	}

	public void setSyncedValue(T value) {
		this.syncedValue = Optional.of(value);
	}

	public void removeSync() {
		this.syncedValue = Optional.empty();
	}

	public boolean isUnsaved() {
		return this.dirty;
	}

	public boolean isSaved() {
		return !this.dirty;
	}

	public void markDirty() {
		this.dirty = true;
	}

	public void unmarkDirty() {
		this.dirty = false;
	}

	protected Component getDisplayName() {
		return Component.translatable("option." + this.id.getNamespace() + "." + this.id.getPath().replace("/", "."));
	}

	protected Component getTooltip() {
		return Component.translatable("tooltip." + this.id.getNamespace() + "." + this.id.getPath().replace("/", "."));
	}

	public boolean hasComment() {
		return this.comment != null && this.comment.isPresent() && !this.comment.get().isEmpty();
	}

	public Identifier getId() {
		return this.id;
	}

	public T getDefaultValue() {
		return this.defaultValue;
	}

	public Optional<String> getComment() {
		return this.comment;
	}

	public Codec<T> getCodec() {
		return this.type.getCodec();
	}

	public StreamCodec<? extends ByteBuf, T> getStreamCodec() {
		return this.type.getStreamCodec();
	}
}
