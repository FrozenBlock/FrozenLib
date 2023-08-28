package net.frozenblock.lib.config.api.instance.json;

import blue.endless.jankson.JsonGrammar;
import net.frozenblock.lib.config.api.instance.ConfigSerialization;
import net.minecraft.util.StringRepresentable;

public enum JsonType implements StringRepresentable {
	JSON("json", JsonGrammar.STRICT),
	JSON5("json5", JsonGrammar.JSON5),
	/**
	 * Like JSON5 but it supports not having quotes on keys
	 */
	JSON5_UNQUOTED_KEYS("json5", ConfigSerialization.JSON5_UNQUOTED_KEYS);

	private final String name;
	private final JsonGrammar grammar;

	JsonType(String name, JsonGrammar grammar) {
		this.name = name;
		this.grammar = grammar;
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}

	public JsonGrammar getGrammar() {
		return this.grammar;
	}
}
