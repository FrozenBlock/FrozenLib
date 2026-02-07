/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.config.v2.entry.property;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class EntryProperties {
	private static final EntryProperties DEFAULT = new EntryProperties(true, true, null, null, null, false);
	private static final EntryProperties DEFAULT_UNSYNCABLE = new EntryProperties(false, true, null, null, null, false);
	private static final EntryProperties DEFAULT_UNMODIFIABLE = new EntryProperties(true, false, null, null, null, false);
	private static final EntryProperties DEFAULT_UNSCYNABLE_UNMODIFIABLE = new EntryProperties(false, false, null, null, null, false);
	private final boolean syncable;
	private final boolean modifiable;
	@Nullable
	private final String comment;
	@Nullable
	private final Function<Object, Component> textSupplier;
	@Nullable
	private final VisibilityPredicate visibilityPredicate;
	private final boolean requireRestart;

	public EntryProperties(
		boolean syncable,
		boolean modifiable,
		@Nullable String comment,
		@Nullable Function<Object, Component> textSupplier,
		@Nullable VisibilityPredicate visibilityPredicate,
		boolean requireRestart
	) {
		this.syncable = syncable;
		this.modifiable = modifiable;
		this.comment = comment;
		this.textSupplier = textSupplier;
		this.visibilityPredicate = visibilityPredicate;
		this.requireRestart = requireRestart;
	}

	public static EntryProperties of(boolean syncable, boolean modifiable) {
		if (syncable && modifiable) return DEFAULT;
		if (!syncable && modifiable) return DEFAULT_UNSYNCABLE;
		if (syncable) return DEFAULT_UNMODIFIABLE;
		return DEFAULT_UNSCYNABLE_UNMODIFIABLE;
	}

	public static EntryProperties ofDefault() {
		return DEFAULT;
	}

	public static EntryProperties ofUnsyncable() {
		return DEFAULT_UNSYNCABLE;
	}

	public boolean isSyncable() {
		return this.syncable;
	}

	public boolean isModifiable() {
		return this.modifiable;
	}

	public boolean hasComment() {
		return this.comment != null;
	}

	@Nullable
	public Optional<String> getComment() {
		return Optional.ofNullable(this.comment);
	}

	public boolean hasTextSupplier() {
		return this.textSupplier != null;
	}

	@Nullable
	public Optional<Function<Object, Component>> getTextSupplier() {
		return Optional.ofNullable(this.textSupplier);
	}

	public boolean hasVisibilityPredicate() {
		return this.visibilityPredicate != null;
	}

	public VisibilityPredicate visibilityPredicate() {
		return this.visibilityPredicate;
	}

	public boolean isVisible() {
		if (!this.hasVisibilityPredicate()) return true;
		return this.visibilityPredicate.test();
	}

	public boolean requireRestart() {
		return this.requireRestart;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static Builder defaultBuilder() {
		return new Builder().syncable(true).modifiable(true);
	}

	public static Builder unsyncableBuilder() {
		return new Builder().syncable(false).modifiable(true);
	}

	public static Builder builderOf(boolean syncable, boolean modifiable) {
		return new Builder().syncable(syncable).modifiable(modifiable);
	}

	public static class Builder {
		private boolean syncable;
		private boolean modifiable;
		@Nullable
		private String comment = null;
		@Nullable
		private Function<Object, Component> textSupplier = null;
		@Nullable
		private VisibilityPredicate visibilityPredicate = null;
		private boolean requireRestart;

		private Builder() {
		}

		public Builder syncable(boolean syncable) {
			this.syncable = syncable;
			return this;
		}

		public Builder modifiable(boolean modifiable) {
			this.modifiable = modifiable;
			return this;
		}

		public Builder comment(@Nullable String comment) {
			this.comment = comment;
			return this;
		}

		public Builder textSupplier(@Nullable Function<Object, Component> textSupplier) {
			this.textSupplier = textSupplier;
			return this;
		}

		public Builder visibilityPredicate(@Nullable VisibilityPredicate visibilityPredicate) {
			this.visibilityPredicate = visibilityPredicate;
			return this;
		}

		public Builder requireRestart(boolean requireRestart) {
			this.requireRestart = requireRestart;
			return this;
		}

		public EntryProperties build() {
			return new EntryProperties(this.syncable, this.modifiable, this.comment, this.textSupplier, this.visibilityPredicate, this.requireRestart);
		}
	}
}
