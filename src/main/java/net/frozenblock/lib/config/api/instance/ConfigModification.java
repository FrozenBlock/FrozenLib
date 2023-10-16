package net.frozenblock.lib.config.api.instance;

import net.frozenblock.config.api.registry.ConfigRegistry;
import java.util.function.Consumer;

public record ConfigModification<T>(Config<T> instance, Consumer<T> modifications) {
    public static <T> T modifyConfig(Config<T> config, T original) {
        // clone
        T instance = config.configClass().getConstructor().newInstance();
        copyInto(original, instance);
        

        // modify
        for (ConfigModification modification : ConfigRegistry.getModificationsForConfig(config)) {
            modification.modifications.accept(instance);
        }
        return instance;
    }

    private static <T> void copyInto(T source, T destination) {
        Class<?> clazz = source.getClass();
        while (!clazz.equals(Object.class)) {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    field.set(destination, field.get(source));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            clazz = clazz.getSuperclass();
        }
    }
}