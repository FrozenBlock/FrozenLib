package net.frozenblock.lib.config.impl.entry;

import net.frozenblock.lib.config.api.entry.TypedEntry;
import net.frozenblock.lib.config.api.entry.TypedEntryType;

/**
 * @since 1.7
 */
public class TypedEntryImpl<T> implements TypedEntry<T> {

    private final TypedEntryType<T> type;
    private T value;

    public TypedEntryImpl(TypedEntryType<T> type, T value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public TypedEntryType<T> type() {
        return this.type;
    }

    @Override
    public T value() {
        return this.value;
    }

    @Override
    public void setValue(T value) {
        this.value = value;
    }
}
