package net.frozenblock.lib.loot.mixin.neoforge;

import com.google.gson.JsonElement;
import net.frozenblock.lib.loot.api.FrozenLibLootTableEvents;
import net.frozenblock.lib.loot.impl.FrozenNeoLootTable;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.Validatable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Drives {@link FrozenLibLootTableEvents#ALL_LOADED}, which has no NeoForge equivalent. NeoForge's own
 * {@code LootTableLoadEvent} already drives {@code REPLACE}/{@code MODIFY} (see {@code NeoLootTableEventBridge}),
 * so this only needs to fire once the loot table registry has finished loading for this reload, and to give each
 * loaded {@link LootTable} its registry holder (for {@code MODIFY_DROPS}).
 */
@Mixin(ReloadableServerRegistries.class)
abstract class ReloadableServerRegistriesMixin {

	@SuppressWarnings("unchecked")
	@Inject(method = "lambda$scheduleRegistryLoad$0", at = @At("RETURN"))
	private static <T extends Validatable> void frozenLib$onLootTablesLoaded(
		LootDataType<T> lootDataType,
		RegistryOps<JsonElement> registryOps,
		ResourceManager resourceManager,
		CallbackInfoReturnable<WritableRegistry<?>> cir
	) {
		if (lootDataType != LootDataType.TABLE) return;

		Registry<LootTable> lootTableRegistry = (Registry<LootTable>) cir.getReturnValue();
		lootTableRegistry.listElements().forEach(reference ->
			((FrozenNeoLootTable) reference.value()).frozenLib$setHolder(reference));

		FrozenLibLootTableEvents.ALL_LOADED.invoker().onLootTablesLoaded(resourceManager, lootTableRegistry);
	}
}
