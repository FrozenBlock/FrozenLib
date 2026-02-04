package net.frozenblock.lib.config.v2.config.map;

import net.frozenblock.lib.FrozenLibLogUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import xjs.data.JsonObject;
import xjs.data.JsonValue;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class XjsMap implements Map<String, JsonValue> {

	private final JsonObject json;

	public XjsMap(JsonObject json) {
		this.json = json;
	}

	@Override
	public int size() {
		return json.size();
	}

	@Override
	public boolean isEmpty() {
		return json.isEmpty();
	}

	@Override
	public boolean containsKey(Object o) {
		return json.keys().contains(o.toString());
	}

	@Override
	public boolean containsValue(Object o) {
		return json.contains(o.toString());
	}

	@Override
	public JsonValue get(Object o) {
		return json.get(o.toString());
	}

	@Override
	public @Nullable JsonValue put(String s, JsonValue jsonValue) {
		return json.add(s, jsonValue);
	}

	@Override
	public JsonValue remove(Object o) {
		return json.remove(o.toString());
	}

	@Override
	public void putAll(@NonNull Map<? extends String, ? extends JsonValue> map) {
		for (var entry : map.entrySet()) {
			json.add(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void clear() {
		json.clear();
	}

	// impl not needed
	@Override
	public @NonNull Set<String> keySet() {
		FrozenLibLogUtils.logWarning("keySet() called on XjsMap");
		return Set.of();
	}

	// impl not needed
	@Override
	public @NonNull Collection<JsonValue> values() {
		FrozenLibLogUtils.logWarning("values() called on XjsMap");
		return List.of();
	}

	// impl not needed
	@Override
	public @NonNull Set<Entry<String, JsonValue>> entrySet() {
		FrozenLibLogUtils.logWarning("entrySet() called on XjsMap");
		return Set.of();
	}
}
