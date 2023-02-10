package net.frozenblock.lib.config.api.entry;

public class TypedEntry<T> {

	private final TypedEntryType<T> type;

	private final T defaultValue;

	private T value;

	public TypedEntry(TypedEntryType<T> type, T defaultValue) {
		this.type = type;
		this.defaultValue = defaultValue;

		this.value = defaultValue;
	}

	public TypedEntryType<T> getType() {
		return this.type;
	}

	public T getDefaultValue() {
		return this.defaultValue;
	}

	public T getValue() {
		return this.value;
	}

	public void setValue(T value) {
		this.value = value;
	}
}
