/*
 * Copyright (C) 2024-2025 FrozenBlock
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

package net.frozenblock.lib.config.api.instance.xjs;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;
import xjs.data.Json;
import xjs.data.JsonArray;
import xjs.data.JsonLiteral;
import xjs.data.JsonObject;
import xjs.data.JsonValue;

/*
 Source: https://github.com/PersonTheCat/CatLib
 License: GNU GPL-3.0
 */
@SuppressWarnings("unused")
public class XjsOps implements DynamicOps<JsonValue> {
	public static final XjsOps INSTANCE = new XjsOps(false);
	public static final XjsOps COMPRESSED = new XjsOps(true);

	private static final JsonValue EMPTY = JsonLiteral.jsonNull();

	private final boolean compressed;

	private XjsOps(final boolean compressed) {
		this.compressed = compressed;
	}

	@Override
	public JsonValue empty() {
		return EMPTY;
	}

	@Override
	public <U> U convertTo(final DynamicOps<U> outOps, final JsonValue input) {
		if (input == null || input.isNull()) return outOps.empty();
		if (input.isObject()) return this.convertMap(outOps, input);
		if (input.isArray()) return this.convertList(outOps, input);
		if (input.isString()) return outOps.createString(input.asString());
		if (input.isBoolean()) return outOps.createBoolean(input.asBoolean());
		if (input.isNumber()) return this.toNumber(outOps, input.asDouble());
		return null;
	}

	private <U> U toNumber(final DynamicOps<U> outOps, final double number) {
		if ((byte) number == number) return outOps.createByte((byte) number);
		if ((short) number == number) return outOps.createShort((short) number);
		if ((int) number == number) return outOps.createInt((int) number);
		if ((float) number == number) return outOps.createFloat((float) number);
		return outOps.createDouble(number);
	}

	@Override
	public DataResult<Number> getNumberValue(final JsonValue input) {
		if (input == null || input.isNull()) return DataResult.error(() -> "Not a number: null");
		if (input.isNumber()) return DataResult.success(input.asDouble());
		if (input.isBoolean()) return DataResult.success(input.asBoolean() ? 1 : 0);
		if (this.compressed && input.isString()) {
			try {
				return DataResult.success(Integer.parseInt(input.asString()));
			} catch (final NumberFormatException e) {
				return DataResult.error(() -> "Not a number: " + e + " " + input);
			}
		}
		return DataResult.error(() -> "Not a number: " + input);
	}

	@Override
	public JsonValue createNumeric(final Number i) {
		return Json.value(i.doubleValue());
	}

	@Override
	public DataResult<Boolean> getBooleanValue(final JsonValue input) {
		if (input == null || input.isNull()) return DataResult.error(() -> "Not a boolean: null");
		if (input.isBoolean()) return DataResult.success(input.asBoolean());
		if (input.isNumber()) return DataResult.success(input.asDouble() != 0);
		return DataResult.error(() -> "Not a boolean: " + input);
	}

	@Override
	public JsonValue createBoolean(final boolean value) {
		return Json.value(value);
	}

	@Override
	public DataResult<String> getStringValue(final JsonValue input) {
		if (input == null || input.isNull()) return DataResult.error(() -> "Not a string: null");
		if (input.isString()) return DataResult.success(input.asString());
		if (this.compressed && input.isNumber()) return DataResult.success(String.valueOf(input.asDouble()));
		return DataResult.error(() -> "Not a string: " + input);
	}

	@Override
	public JsonValue createString(final String value) {
		return Json.value(value);
	}

	@Override
	public DataResult<JsonValue> mergeToList(final JsonValue list, final JsonValue value) {
		if (list == null || list.isNull()) return DataResult.success(new JsonArray().add(value));
		if (list.isArray()) return DataResult.success(new JsonArray().addAll(list.asArray()).add(value));
		return DataResult.error(() -> "mergeToList called with not a list: " + list, list);
	}

	@Override
	public DataResult<JsonValue> mergeToList(final JsonValue list, final List<JsonValue> values) {
		if (list == null || list.isNull()) {
			final JsonArray result = new JsonArray();
			values.forEach(result::add);
			return DataResult.success(result);
		} else if (list.isArray()) {
			final JsonArray result = (JsonArray) list.asArray().shallowCopy();
			values.forEach(result::add);
			return DataResult.success(result);
		}
		return DataResult.error(() -> "mergeToList called with not a list: " + list, list);
	}

	@Override
	public DataResult<JsonValue> mergeToMap(final JsonValue map, final JsonValue key, final JsonValue value) {
		if (!(map == null || map.isObject() || map.isNull())) return DataResult.error(() -> "mergeToMap called with not a map: " + map, map);

		if (!(key.isString() || (this.compressed && isPrimitiveLike(key)))) {
			final String msg = "key is not a string: " + key;
			return map != null ? DataResult.error(() -> msg, map) : DataResult.error(() -> msg);
		}

		if (map == null || map.isNull()) return DataResult.success(new JsonObject().add(asPrimitiveString(key), value));
		return DataResult.success(new JsonObject().addAll(map.asObject()).add(asPrimitiveString(key), value));
	}

	@Override
	public DataResult<JsonValue> mergeToMap(final JsonValue map, MapLike<JsonValue> values) {
		if (!(map == null || map.isObject() || map.isNull())) return DataResult.error(() -> "mergeToMap called with not a map: " + map, map);

		final JsonObject output = new JsonObject();
		if (map != null && map.isObject()) output.addAll(map.asObject());

		final List<JsonValue> missed = new ArrayList<>();
		values.entries().forEach(entry -> {
			final JsonValue key = entry.getFirst();
			if (key.isString() || (this.compressed && isPrimitiveLike(key))) {
				output.add(asPrimitiveString(key), entry.getSecond());
			} else {
				missed.add(key);
			}
		});
		if (!missed.isEmpty()) return DataResult.error(() -> "some keys are not strings: " + missed, output);
		return DataResult.success(output);
	}

	@Override
	public DataResult<Stream<Pair<JsonValue, JsonValue>>> getMapValues(final JsonValue input) {
		if (input == null || !input.isObject()) return DataResult.error(() -> "Not an XJS object: " + input);

		final Stream.Builder<Pair<JsonValue, JsonValue>> builder = Stream.builder();
		for (final JsonObject.Member member : input.asObject()) {
			final JsonValue value = member.getValue();
			builder.add(Pair.of(Json.value(member.getKey()), value.isNull() ? null : value));
		}
		return DataResult.success(builder.build());
	}

	@Override
	public DataResult<Consumer<BiConsumer<JsonValue, JsonValue>>> getMapEntries(final JsonValue input) {
		if (input == null || !input.isObject()) return DataResult.error(() -> "Not an XJS object: " + input);

		return DataResult.success(c -> {
			for (final JsonObject.Member member : input.asObject()) {
				final JsonValue value = member.getValue();
				c.accept(Json.value(member.getKey()), value.isNull() ? null : value);
			}
		});
	}

	@Override
	public DataResult<MapLike<JsonValue>> getMap(final JsonValue input) {
		if (input == null || !input.isObject()) return DataResult.error(() -> "Not an XJS object: " + input);
		return DataResult.success(new XJSMapLike(input.asObject()));
	}

	@Override
	public JsonValue createMap(final Stream<Pair<JsonValue, JsonValue>> map) {
		final JsonObject result = new JsonObject();
		map.forEach(p -> {
			final JsonValue v = p.getSecond();
			result.add(p.getFirst().asString(), v != null ? v : EMPTY);
		});
		return result;
	}

	@Override
	public DataResult<Stream<JsonValue>> getStream(final JsonValue input) {
		if (input == null || !input.isArray()) return DataResult.error(() -> "Not an XJS array: " + input);

		final Stream.Builder<JsonValue> builder = Stream.builder();
		for (final JsonValue value : input.asArray()) {
			builder.add(value.isNull() ? null : value);
		}
		return DataResult.success(builder.build());
	}

	@Override
	public DataResult<Consumer<Consumer<JsonValue>>> getList(final JsonValue input) {
		if (input == null || !input.isArray()) return DataResult.error(() -> "Not an XJS array: + " + input);

		return DataResult.success(c -> {
			for (final JsonValue value : input.asArray()) {
				c.accept(value.isNull() ? null : value);
			}
		});
	}

	@Override
	public JsonValue createList(final Stream<JsonValue> input) {
		final JsonArray result = new JsonArray();
		input.forEach(v -> result.add(v != null ? v : EMPTY));
		return result;
	}

	@Override
	public JsonValue remove(final JsonValue input, final String key) {
		if (input == null || !input.isObject()) return input;

		final JsonObject result = new JsonObject();
		for (final JsonObject.Member member : input.asObject()) {
			if (member.getKey().equals(key)) continue;
			result.add(member.getKey(), member.getValue());
		}
		return result;
	}

	@Override
	public boolean compressMaps() {
		return this.compressed;
	}

	@Override
	public String toString() {
		return "XJS";
	}

	private record XJSMapLike(JsonObject object) implements MapLike<JsonValue> {

		@Nullable
		@Override
		public JsonValue get(final JsonValue key) {
			final JsonValue value = this.object.get(key.asString());
			return value != null && value.isNull() ? null : value;
		}

		@Nullable
		@Override
		public JsonValue get(final String key) {
			final JsonValue value = this.object.get(key);
			return value != null && value.isNull() ? null : value;
		}

		@Override
		public Stream<Pair<JsonValue, JsonValue>> entries() {
			final Stream.Builder<Pair<JsonValue, JsonValue>> builder = Stream.builder();
			for (final JsonObject.Member member : this.object) {
				final JsonValue value = member.getValue();
				builder.add(Pair.of(Json.value(member.getKey()), value.isNull() ? null : value));
			}
			return builder.build();
		}

		@Override
		public String toString() {
			return "XJSMapLike[" + this.object + "]";
		}
	}

	private static boolean isPrimitiveLike(final JsonValue value) {
		return value.isBoolean() || value.isString() || value.isNumber();
	}

	private static String asPrimitiveString(final JsonValue value) {
		return value.isNumber() ? String.valueOf(value.asDouble()) : value.asString();
	}
}
