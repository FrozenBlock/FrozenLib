package net.frozenblock.lib.platform.attribute;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.frozenblock.lib.platform.service.DefaultAttributeRegistryHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

public class FabricDefaultAttributeRegistryHelper implements DefaultAttributeRegistryHelper {

	@Override
	public void register(EntityType<? extends LivingEntity> type, AttributeSupplier.Builder builder) {
		FabricDefaultAttributeRegistry.register(type, builder);
	}

	@Override
	public void register(EntityType<? extends LivingEntity> type, AttributeSupplier container) {
		FabricDefaultAttributeRegistry.register(type, container);
	}
}
