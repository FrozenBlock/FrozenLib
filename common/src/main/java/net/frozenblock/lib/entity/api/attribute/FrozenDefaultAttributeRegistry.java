package net.frozenblock.lib.entity.api.attribute;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.platform.FrozenLibInitPlatformUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

@UtilityClass
public class FrozenDefaultAttributeRegistry {

	public static void register(EntityType<? extends LivingEntity> type, AttributeSupplier.Builder builder) {
		FrozenLibInitPlatformUtils.DEFAULT_ATTRIBUTE_REGISTRY.register(type, builder);
	}

	public static void register(EntityType<? extends LivingEntity> type, AttributeSupplier container) {
		FrozenLibInitPlatformUtils.DEFAULT_ATTRIBUTE_REGISTRY.register(type, container);
	}
}
