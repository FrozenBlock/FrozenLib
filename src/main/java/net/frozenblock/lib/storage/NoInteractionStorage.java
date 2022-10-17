package net.frozenblock.lib.storage;

import java.util.Iterator;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public interface NoInteractionStorage<T> extends Storage<T> {

    @Override
    default long insert(T resource, long maxAmount, TransactionContext transaction) {
        return 0;
    }

    @Override
    default long extract(T resource, long maxAmount, TransactionContext transaction) {
        return 0;
    }

    @Override
    default boolean supportsInsertion() {
        return false;
    }

    @Override
    default boolean supportsExtraction() {
        return false;
    }

    @Override
    default Iterator<StorageView<T>> iterator() {
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public StorageView<T> next() {
                return null;
            }
        };
    }
}
