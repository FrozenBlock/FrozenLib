package net.frozenblock.lib.config.newconfig.modification;

import net.frozenblock.lib.config.newconfig.entry.ConfigEntry;
import net.frozenblock.lib.config.api.instance.util.DeepCopyUtils;

import java.util.function.Consumer;

public record ConfigEntryModification<T>(Consumer<EntryValueHolder<T>> modifier) {

    public static <T> T modifyEntry(ConfigEntry<T> entry, T original) {
        final T copy;
        try {
            // Use DeepCopyUtils to create a shallow instance and later copy into it
            copy = DeepCopyUtils.deepCopy(original);
        } catch (Exception e) {
            throw new RuntimeException("Failed to copy config entry value", e);
        }

        copyInto(original, copy);
        final EntryValueHolder<T> holder = new EntryValueHolder<>(original);

        for (ConfigEntryModification<T> modifications : entry.modifications()) {
            modifications.modifier().accept(holder);
        }
        return holder.value;
    }

	public static <T> void copyInto(T source, T destination) {
		DeepCopyUtils.deepCopyInto(source, destination);
	}
}
