package net.frozenblock.lib.platform.attribute;

import java.util.ArrayList;
import java.util.List;
import net.frozenblock.lib.platform.service.DefaultAttributeRegistryHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

public class NeoDefaultAttributeRegistryHelper implements DefaultAttributeRegistryHelper {
	private record Entry(EntityType<? extends LivingEntity> type, AttributeSupplier container) {}

	private static final List<Entry> ENTRIES = new ArrayList<>();

	@Override
	public void register(EntityType<? extends LivingEntity> type, AttributeSupplier.Builder builder) {
		this.register(type, builder.build());
	}

	@Override
	public void register(EntityType<? extends LivingEntity> type, AttributeSupplier container) {
		ENTRIES.add(new Entry(type, container));
	}

	public static void flush(EntityAttributeCreationEvent event) {
		for (Entry entry : ENTRIES) {
			event.put(entry.type(), entry.container());
		}
	}
}
