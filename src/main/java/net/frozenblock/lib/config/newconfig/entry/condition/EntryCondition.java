package net.frozenblock.lib.config.newconfig.entry.condition;

import java.util.function.Predicate;
import net.frozenblock.lib.config.newconfig.entry.ConfigEntry;

public class EntryCondition<T> extends AbstractCondition {
	private final ConfigEntry<T> entry;
	private final Predicate<ConfigEntry<T>> predicate;

	public EntryCondition(ConfigEntry<T> entry, Predicate<ConfigEntry<T>> predicate) {
		this.entry = entry;
		this.predicate = predicate;
	}

	@Override
	public boolean test() {
		return this.predicate.test(this.entry);
	}
}
