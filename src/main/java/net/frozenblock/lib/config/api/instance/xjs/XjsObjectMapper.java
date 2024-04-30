/*
 * Copyright 2024 The Quilt Project
 * Copyright 2024 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.config.api.instance.xjs;

import blue.endless.jankson.Comment;
import blue.endless.jankson.annotation.SaveToggle;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import me.shedaniel.autoconfig.util.Utils;
import net.frozenblock.lib.config.api.entry.TypedEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xjs.data.Json;
import xjs.data.JsonArray;
import xjs.data.JsonObject;
import xjs.data.JsonValue;

/*
 Source: https://github.com/PersonTheCat/CatLib
 License: GNU GPL-3.0
 */
public class XjsObjectMapper {

	public static void serializeObject(final Path p, final Object o) throws IOException, NonSerializableObjectException {
		XjsUtils.writeJson(toJsonObject(o), p.toFile()).throwIfErr();
	}

	public static <T> T deserializeObject(final Path p, final Class<T> clazz) throws NonSerializableObjectException {
		return deserializeObject(null, p, clazz);
	}

	public static <T> T deserializeObject(final @Nullable String modId, final Path p, final Class<T> clazz) throws NonSerializableObjectException {
		final T t = Utils.constructUnsafely(clazz);

		final Optional<JsonObject> read = XjsUtils.readJson(p.toFile());
		if (read.isEmpty()) return t;
		final JsonObject json = read.get();
		if (json.isEmpty()) return t;

		writeObjectInto(modId, t, json);

		return t;
	}

	public static JsonValue toJsonValue(final Object o) throws NonSerializableObjectException {
		if (o.getClass().isArray()) {
			return toJsonArray((Object[]) o);
		} else if (o.getClass().isEnum()) {
			return Json.value(((Enum<?>) o).name());
		} else if (o instanceof String) {
			return Json.value((String) o);
		} else if (o instanceof Integer) {
			return Json.value((Integer) o);
		} else if (o instanceof Long) {
			return Json.value((Long) o);
		} else if (o instanceof Float || o instanceof Double) {
			return Json.value(((Number) o).doubleValue());
		} else if (o instanceof Boolean) {
			return Json.value((Boolean) o);
		} else if (o instanceof Collection) {
			return toJsonArray((Collection<?>) o);
		} else if (o instanceof Map) {
			return toJsonObject((Map<?, ?>) o);
		} else if (o instanceof TypedEntry) {
			return XjsTypedEntrySerializer.toJsonValue((TypedEntry<?>) o);
		}
		return toJsonObject(o);
	}

	public static JsonObject toJsonObject(final Object o) throws NonSerializableObjectException {
		final JsonObject json = new JsonObject();

		final Class<?> c = o.getClass();
		for (final Field f : c.getDeclaredFields()) {
			if (!Modifier.isStatic(f.getModifiers()) && !Modifier.isTransient(f.getModifiers())) {
				final JsonValue value = toJsonValue(Utils.getUnsafely(f, o));

				final String comment = getComment(f);
				if (comment != null) value.setComment(comment);

				if (getSaveToggle(f)) {
					json.add(f.getName(), value);
				}
			}
		}
		return json;
	}

	@Nullable
	public static String getComment(final Field f) {
		final Comment[] comments = f.getAnnotationsByType(Comment.class);
		if (comments.length == 0) return null;
		return comments[0].value();
	}

	public static boolean getSaveToggle(final Field f) {
		final SaveToggle[] toggles = f.getAnnotationsByType(SaveToggle.class);
		if (toggles.length == 0) return true;
		return toggles[0].value();
	}

	public static JsonObject toJsonObject(final Map<?, ?> map) throws NonSerializableObjectException {
		final JsonObject json = new JsonObject();
		for (final Map.Entry<?, ?> entry : map.entrySet()) {
			if (!(entry.getKey() instanceof String)) {
				throw NonSerializableObjectException.unsupportedKey(entry.getKey());
			}
			json.add((String) entry.getKey(), toJsonValue(entry.getValue()));
		}
		return json;
	}

	public static JsonArray toJsonArray(final Object[] a) throws NonSerializableObjectException {
		final JsonArray json = new JsonArray();
		for (final Object o : a) {
			json.add(toJsonValue(o));
		}
		return json;
	}

	public static JsonArray toJsonArray(final Collection<?> a) throws NonSerializableObjectException {
		final JsonArray json = new JsonArray();
		if (a.isEmpty()) return json;
		for (final Object o : a) {
			json.add(toJsonValue(o));
		}
		return json;
	}

	private static void writeObjectInto(final @Nullable String modId, final Object o, final JsonObject json) throws NonSerializableObjectException {
		final Class<?> clazz = o.getClass();
		for (final JsonObject.Member member : json) {
			final Field f = getField(clazz, member.getKey());
			if (f == null || !getSaveToggle(f)) continue;

			final Object def = Utils.getUnsafely(f, o);
			Utils.setUnsafely(f, o, getValueByType(modId, f.getType(), def, member.getValue()));
		}
	}

	private static Field getField(final Class<?> clazz, final String name) {
		for (final Field f : clazz.getDeclaredFields()) {
			if (name.equals(f.getName())) {
				return f;
			}
		}
		return null;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static Object getValueByType(final @Nullable String modId, final Class<?> type, final Object def, final JsonValue value) throws NonSerializableObjectException {
		if (type.isAssignableFrom(TypedEntry.class)) {
			return XjsTypedEntrySerializer.fromJsonValue(modId, value);
		} else if (type.isArray()) {
			return toArray(modId, type, def, value);
		} else if (type.isEnum()) {
			return assertEnumConstant(value.asString(), (Class) type);
		} else if (type.isAssignableFrom(String.class)) {
			return value.asString();
		} else if (type.isAssignableFrom(Integer.class) || type.isAssignableFrom(int.class)) {
			return value.asInt();
		} else if (type.isAssignableFrom(Long.class) || type.isAssignableFrom(long.class)) {
			return value.asLong();
		} else if (type.isAssignableFrom(Float.class) || type.isAssignableFrom(float.class)) {
			return value.asFloat();
		} else if (type.isAssignableFrom(Double.class) || type.isAssignableFrom(double.class)) {
			return value.asDouble();
		} else if (type.isAssignableFrom(Boolean.class) || type.isAssignableFrom(boolean.class)) {
			return value.asBoolean();
		} else if (type.isAssignableFrom(List.class)) {
			return toList(modId, value, def);
		} else if (type.isAssignableFrom(Set.class)) {
			return new HashSet<>(toList(modId, value, def));
		} else if (type.equals(Collection.class)) {
			return toList(modId, value, def);
		} else if (type.isAssignableFrom(Map.class)) {
			return toMap(modId, value, def);
		}
		final Object o = Utils.constructUnsafely(type);
		writeObjectInto(modId, o, value.asObject());
		return o;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static Object toArray(final @Nullable String modId, final Class<?> type, Object def, final JsonValue value) throws NonSerializableObjectException {
		final JsonArray json = value.asArray();
		final Object[] array = new Object[json.size()];

		final Object[] defaults = (Object[]) def;
		def = defaults != null && defaults.length > 0 ? defaults[0] : null;

		for (int i = 0; i < json.size(); i++) {
			array[i] = getValueByType(modId, type.getComponentType(), def, json.get(i));
		}
		return Arrays.copyOf(array, array.length, (Class) type);
	}

	private static List<Object> toList(final @Nullable String modId, final JsonValue value, Object def) throws NonSerializableObjectException {
		final Collection<?> defaults = (Collection<?>) def;
		def = defaults != null && !defaults.isEmpty() ? defaults.iterator().next() : null;

		if (def == null) throw NonSerializableObjectException.defaultRequired();

		final List<Object> list = new ArrayList<>();
		for (final JsonValue v : value.asArray()) {
			list.add(getValueByType(modId, def.getClass(), def, v));
		}
		return list;
	}

	private static Map<String, Object> toMap(final @Nullable String modId, final JsonValue value, Object def) throws NonSerializableObjectException {
		final Map<String, Object> map = new HashMap<>();

		final Map<?, ?> defaults = (Map<?, ?>) def;
		def = defaults != null && !defaults.isEmpty() ? defaults.entrySet().iterator().next().getValue() : null;

		if (def == null) throw NonSerializableObjectException.defaultRequired();

		for (final JsonObject.Member member : value.asObject()) {
			map.put(member.getKey(), getValueByType(modId, def.getClass(), def, member.getValue()));
		}
		return map;
	}

	/**
	 * Uses a linear search algorithm to locate a value in an array, matching
	 * the predicate `by`. Shorthand for Stream#findFirst.
	 *
	 * <p>Example:</p>
	 * <pre>{@code
	 *    // Find x by x.name
	 *    Object[] vars = getObjectsWithNames();
	 *    Optional<Object> var = find(vars, (x) -> x.name.equals("Cat"));
	 *    // You can then get the value -> NPE
	 *    Object result = var.get()
	 *    // Or use an alternative. Standard java.util.Optional. -> no NPE
	 *    Object result = var.orElse(new Object("Cat"))
	 * }</pre>
	 *
	 * @param <T> The type of array being passed in.
	 * @param values The actual array containing the value.
	 * @param by A predicate which determines which value to return.
	 * @return The value, or else {@link Optional#empty}.
	 */
	@NotNull
	private static <T> Optional<T> find(final T[] values, final Predicate<T> by) {
		for (final T val : values) {
			if (by.test(val)) {
				return Optional.of(val);
			}
		}
		return Optional.empty();
	}

	/**
	 * Retrieves an enum constant by name.
	 *
	 * @throws InvalidEnumConstantException If the given key is invalid.
	 * @param s The name of the constant being researched.
	 * @param clazz The enum class which contains the expected constant.
	 * @param <T> The type of constant being researched.
	 * @return The expected constant.
	 */
	@NotNull
	private static <T extends Enum<T>> T assertEnumConstant(final String s, final Class<T> clazz) {
		return getEnumConstant(s, clazz).orElseThrow(() -> new InvalidEnumConstantException(s, clazz));
	}

	/**
	 * Retrieves an enum constant by name.
	 *
	 * @param s The name of the constant being researched.
	 * @param clazz The enum class which contains the expected constant.
	 * @param <E> The type of constant being researched.
	 * @return The expected constant, or else {@link Optional#empty}.
	 */
	private static <E extends Enum<E>> Optional<E> getEnumConstant(final String s, final Class<E> clazz) {
		return find(clazz.getEnumConstants(), e -> isFormatted(e, s));
	}

	/**
	 * Determines whether a string matches the given enum constant's name, ignoring
	 * case and underscores (<code>_</code>).
	 *
	 * @param e The enum constant being compared.
	 * @param s The string identifier for this constant.
	 * @param <E> The type of enum value.
	 * @return Whether this string is a valid identifier for the constant.
	 */
	private static <E extends Enum<E>> boolean isFormatted(final E e, final String s) {
		final String id = e.name().replace("_", "");
		return id.equalsIgnoreCase(s.replace("_", ""));
	}
}
