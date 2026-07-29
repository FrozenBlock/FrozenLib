/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.levelgen.attribute.api;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.platform.api.registry.FrozenDeferredRegister;
import net.frozenblock.lib.platform.api.registry.FrozenHolder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.attribute.AttributeRange;
import net.minecraft.world.attribute.AttributeTypes;
import net.minecraft.world.attribute.EnvironmentAttribute;

@UtilityClass
public final class FrozenLibEnvironmentAttributes {
	private static final FrozenDeferredRegister<EnvironmentAttribute<?>> REGISTER = FrozenDeferredRegister.create(
		Registries.ENVIRONMENT_ATTRIBUTE,
		FrozenLibConstants.MOD_ID
	);

	public static final FrozenHolder<EnvironmentAttribute<?>, EnvironmentAttribute<Float>> LIGHTMAP_BRIGHTNESS = register(
		"visual/lightmap_brightness",
		EnvironmentAttribute.builder(AttributeTypes.FLOAT).defaultValue(1F).valueRange(AttributeRange.UNIT_FLOAT).spatiallyInterpolated().syncable()
	);

	static {
		REGISTER.register();
	}

	public static void init() {}

	private static <Value> FrozenHolder<EnvironmentAttribute<?>, EnvironmentAttribute<Value>> register(String name, EnvironmentAttribute.Builder<Value> attributeBuilder) {
		return REGISTER.register(name, attributeBuilder::build);
	}
}
