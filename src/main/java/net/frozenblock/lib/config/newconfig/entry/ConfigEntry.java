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
import net.frozenblock.lib.config.newconfig.entry.property.EntryProperties;
import net.frozenblock.lib.config.newconfig.entry.property.VisibilityPredicate;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public class ConfigEntry<T> {
	private final Identifier id;
	private final EntryType<T> type;
	private final T defaultValue;
	private final EntryProperties properties;

	private T value;
	private Optional<T> syncedValue = Optional.empty();
	private boolean dirty;
	private boolean hasCheckedLoad;

	public ConfigEntry(Identifier id, EntryType<T> type, T defaultValue, EntryProperties properties) {
		this.id = id;
		this.type = type;
		this.defaultValue = defaultValue;
		this.value = defaultValue;
		this.properties = properties;
		Registry.register(FrozenLibRegistries.CONFIG_ENTRY, id, this);
	}

	public ConfigEntry(Identifier id, EntryType<T> type, T defaultValue) {
		this(id, type, defaultValue, EntryProperties.ofDefault());
	}

	public ConfigEntry(Identifier id, EntryType<T> type, T defaultValue, boolean syncable, boolean modifiable) {
		this(id, type, defaultValue, EntryProperties.of(syncable, modifiable));
	}

	public static <T> ConfigEntry<T> unsyncable(Identifier id, EntryType<T> type, T defaultValue) {
		return new ConfigEntry<>(id, type, defaultValue, false, true);
	}

	public static <T> ConfigEntry<T> unmodifiable(Identifier id, EntryType<T> type, T defaultValue) {
		return new ConfigEntry<>(id, type, defaultValue, true, false);
	}

	public static <T> ConfigEntry<T> unsyncableAndUnmodifiable(Identifier id, EntryType<T> type, T defaultValue) {
		return new ConfigEntry<>(id, type, defaultValue, false, false);
	}

	public static <T> Builder<T> builder(Identifier id, EntryType<T> type, T defaultValue) {
		return builder(id, type, defaultValue, true, true);
	}

	public static <T> Builder<T> unsyncableBuilder(Identifier id, EntryType<T> type, T defaultValue) {
		return builder(id, type, defaultValue, false, true);
	}

	public static <T> Builder<T> unmodifiableBuilder(Identifier id, EntryType<T> type, T defaultValue) {
		return builder(id, type, defaultValue, true, false);
	}

	public static <T> Builder<T> builder(Identifier id, EntryType<T> type, T defaultValue, boolean syncable, boolean modifiable) {
		return new Builder<T>().id(id).type(type).defaultValue(defaultValue).properties(EntryProperties.builderOf(true, false));
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
		if (!this.properties.isSyncable()) return;
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
		return this.properties.hasComment();
	}

	public Identifier getId() {
		return this.id;
	}

	public T getDefaultValue() {
		return this.defaultValue;
	}

	public Optional<String> getComment() {
		return this.properties.getComment();
	}

	public Codec<T> getCodec() {
		return this.type.getCodec();
	}

	public StreamCodec<? extends ByteBuf, T> getStreamCodec() {
		return this.type.getStreamCodec();
	}

	public static class Builder<T> {
		Identifier id = null;
		EntryType<T> type = null;
		T defaultValue = null;
		EntryProperties.Builder properties = EntryProperties.builder();

		private Builder() {
		}

		public Builder<T> id(Identifier id) {
			this.id = id;
			return this;
		}

		public Builder<T> type(EntryType<T> type) {
			this.type = type;
			return this;
		}

		public Builder<T> defaultValue(T defaultValue) {
			this.defaultValue = defaultValue;
			return this;
		}

		public Builder<T> properties(EntryProperties.Builder properties) {
			this.properties = properties;
			return this;
		}

		public Builder<T> syncable(boolean syncable) {
			this.properties.syncable(syncable);
			return this;
		}

		public Builder<T> modifiable(boolean modifiable) {
			this.properties.modifiable(modifiable);
			return this;
		}

		public Builder<T> comment(String comment) {
			this.properties.comment(comment);
			return this;
		}

		public Builder<T> visibilityPredicate(VisibilityPredicate predicate) {
			this.properties.visibilityPredicate(predicate);
			return this;
		}

		public ConfigEntry<T> build() {
			if (this.id == null) throw new IllegalStateException("Entry ID cannot be null!");
			if (this.type == null) throw new IllegalStateException("Entry type cannot be null!");
			if (this.defaultValue == null) throw new IllegalStateException("Entry Default value cannot be null!");
			if (this.properties == null) throw new IllegalStateException("Entry Properties cannot be null!");

			return new ConfigEntry<>(this.id, this.type, this.defaultValue, this.properties.build());
		}
	}
}
