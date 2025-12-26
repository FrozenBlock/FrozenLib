package net.frozenblock.lib.config.newconfig.entry.property;

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
