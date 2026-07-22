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
