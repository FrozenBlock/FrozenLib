package net.frozenblock.lib.config.api.entry;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class ConfigExclusionStrategy implements ExclusionStrategy {
	@Override
	public boolean shouldSkipField(FieldAttributes attributes) {
		return attributes.getAnnotation(Exclude.class) != null;
	}

	@Override
	public boolean shouldSkipClass(Class<?> var) {
		return false;
	}
}
