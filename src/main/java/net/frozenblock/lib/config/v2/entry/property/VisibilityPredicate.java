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

import java.util.function.Supplier;

public class VisibilityPredicate {
	private final Supplier<Boolean> predicate;

	private VisibilityPredicate(Supplier<Boolean> predicate) {
		this.predicate = predicate;
	}

	public static VisibilityPredicate of(Supplier<Boolean> predicate) {
		return new VisibilityPredicate(predicate);
	}

	public boolean test() {
		return this.predicate.get();
	}

	public static Builder builder(Supplier<Boolean> predicate) {
		return new Builder(predicate);
	}

	private static class Builder {
		private Supplier<Boolean> predicate;

		private Builder(Supplier<Boolean> predicate) {
			this.predicate = predicate;
		}

		public Builder and(Supplier<Boolean> predicate) {
			this.predicate = () -> this.predicate.get() && predicate.get();
			return this;
		}

		public Builder or(Supplier<Boolean> predicate) {
			this.predicate = () -> this.predicate.get() || predicate.get();
			return this;
		}

		public Builder not() {
			this.predicate = () -> !this.predicate.get();
			return this;
		}
	}
}
