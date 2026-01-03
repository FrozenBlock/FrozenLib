package net.frozenblock.lib.config.api.instance.util;

import net.frozenblock.lib.config.impl.network.ConfigSyncModification;
import net.frozenblock.lib.config.api.instance.xjs.UnsafeUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public final class DeepCopyUtils {

    private static final Set<Class<?>> IMMUTABLES = new HashSet<>(Arrays.asList(
        String.class,
        Boolean.class, Byte.class, Short.class, Character.class, Integer.class, Long.class, Float.class, Double.class,
        Void.class, Class.class
    ));

    private DeepCopyUtils() {}

    @SuppressWarnings("unchecked")
    public static <T> T deepCopy(T source) {
        if (source == null) return null;
        Map<Object, Object> seen = new IdentityHashMap<>();
        return (T) deepCopyInternal(source, seen, false);
    }

    public static <T> void deepCopyInto(T source, T destination, boolean isSyncModification) {
        if (source == null || destination == null) return;
        Map<Object, Object> seen = new IdentityHashMap<>();
        // Register destination for source root so cycles to root are preserved
        seen.put(source, destination);
        deepCopyIntoInternal(source, destination, seen, isSyncModification);
    }

    private static Object deepCopyInternal(Object source, Map<Object, Object> seen, boolean isSyncModification) {
        if (source == null) return null;

        // Handle previously seen instances (cycles / shared refs)
        if (seen.containsKey(source)) return seen.get(source);

        Class<?> cls = source.getClass();

        // Immutable or primitive wrapper / enum / class
        if (cls.isPrimitive() || IMMUTABLES.contains(cls) || cls.isEnum() || cls.getName().startsWith("java.time.")) {
            return source;
        }

        try {
            // Arrays
            if (cls.isArray()) {
                Class<?> componentType = cls.getComponentType();
                int length = Array.getLength(source);
                Object newArray = Array.newInstance(componentType, length);
                seen.put(source, newArray);

                if (componentType.isPrimitive() || IMMUTABLES.contains(componentType) || componentType.isEnum()) {
                    // primitive or immutable component: copy directly
                    for (int i = 0; i < length; i++) Array.set(newArray, i, Array.get(source, i));
                } else {
                    for (int i = 0; i < length; i++) {
                        Object elem = Array.get(source, i);
                        Object copied = deepCopyInternal(elem, seen, isSyncModification);
                        Array.set(newArray, i, copied);
                    }
                }
                return newArray;
            }

            // Collections
            if (source instanceof Collection) {
                Collection<?> col = (Collection<?>) source;
                Collection<Object> newCol = instantiateCollection(col);
                seen.put(source, newCol);
                for (Object elem : col) {
                    Object copied = deepCopyInternal(elem, seen, isSyncModification);
                    newCol.add(copied);
                }
                return newCol;
            }

            // Maps
            if (source instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) source;
                Map<Object, Object> newMap = instantiateMap(map);
                seen.put(source, newMap);
                for (Map.Entry<?, ?> e : map.entrySet()) {
                    Object k = deepCopyInternal(e.getKey(), seen, isSyncModification);
                    Object v = deepCopyInternal(e.getValue(), seen, isSyncModification);
                    newMap.put(k, v);
                }
                return newMap;
            }

            // Fallback: POJO
            Object newObj = instantiateObject(cls);
            seen.put(source, newObj);

            Class<?> curr = cls;
            while (curr != null && !curr.equals(Object.class)) {
                for (Field field : curr.getDeclaredFields()) {
                    if (Modifier.isStatic(field.getModifiers())) continue;
                    if (isSyncModification && !ConfigSyncModification.isSyncable(field)) continue;

                    field.setAccessible(true);
                    Object value = field.get(source);
                    Object copied = deepCopyInternal(value, seen, isSyncModification);
                    try {
                        field.set(newObj, copied);
                    } catch (IllegalAccessException iae) {
                        // Try UnsafeUtils fallback (uses reflection with accessible field)
                        UnsafeUtils.setUnsafely(field, newObj, copied);
                    }
                }
                curr = curr.getSuperclass();
            }

            return newObj;
        } catch (Exception e) {
            throw new RuntimeException("Deep copy failed for class: " + cls.getName(), e);
        }
    }

    private static void deepCopyIntoInternal(Object source, Object destination, Map<Object, Object> seen, boolean isSyncModification) {
        if (source == null || destination == null) return;
        Class<?> cls = source.getClass();

        // Skip primitives, known immutables, enums and java.time classes: they have no writable fields
        // and trying to set accessibility on JDK types (e.g. java.lang.Boolean.value) triggers
        // InaccessibleObjectException under the Java module system.
        if (cls.isPrimitive() || IMMUTABLES.contains(cls) || cls.isEnum() || cls.getName().startsWith("java.time.")) {
            return;
        }

        try {
            Class<?> curr = cls;
            while (curr != null && !curr.equals(Object.class)) {
                for (Field field : curr.getDeclaredFields()) {
                    if (Modifier.isStatic(field.getModifiers())) continue;
                    if (isSyncModification && !ConfigSyncModification.isSyncable(field)) continue;

                    field.setAccessible(true);
                    Object value = field.get(source);
                    Object copied;
                    if (value == null) {
                        copied = null;
                    } else if (seen.containsKey(value)) {
                        copied = seen.get(value);
                    } else {
                        copied = deepCopyInternal(value, seen, isSyncModification);
                    }

                    try {
                        field.set(destination, copied);
                    } catch (IllegalAccessException iae) {
                        UnsafeUtils.setUnsafely(field, destination, copied);
                    }
                }
                curr = curr.getSuperclass();
            }
        } catch (Exception e) {
            throw new RuntimeException("Deep copy-into failed for class: " + cls.getName(), e);
        }
    }

    private static Collection<Object> instantiateCollection(Collection<?> source) {
        Class<?> cls = source.getClass();
        try {
            @SuppressWarnings("unchecked")
            Collection<Object> c = (Collection<Object>) cls.getDeclaredConstructor().newInstance();
            return c;
        } catch (Exception ignored) {
            // Fallback by interface
            if (source instanceof List) return new ArrayList<>();
            if (source instanceof Set) return new LinkedHashSet<>();
            if (source instanceof Queue) return new ArrayDeque<>();
            return new ArrayList<>();
        }
    }

    private static Map<Object, Object> instantiateMap(Map<?, ?> source) {
        Class<?> cls = source.getClass();
        try {
            @SuppressWarnings("unchecked")
            Map<Object, Object> m = (Map<Object, Object>) cls.getDeclaredConstructor().newInstance();
            return m;
        } catch (Exception ignored) {
            return new LinkedHashMap<>();
        }
    }

    private static Object instantiateObject(Class<?> cls) {
        try {
            Constructor<?> constructor = cls.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            // Fallback to Unsafe.allocateInstance if no accessible constructor
            try {
                Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
                f.setAccessible(true);
                sun.misc.Unsafe unsafe = (sun.misc.Unsafe) f.get(null);
                return unsafe.allocateInstance(cls);
            } catch (Exception ex) {
                throw new RuntimeException("Failed to instantiate class: " + cls.getName(), ex);
            }
        }
    }
}
