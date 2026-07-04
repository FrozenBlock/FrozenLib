package net.frozenblock.lib.item.api.loot;

import io.netty.util.internal.UnstableApi;
import java.util.function.Consumer;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.ApiStatus;

@UnstableApi
@ApiStatus.NonExtendable
public interface FrozenLibLootTableBuilder {

	default LootTable.Builder frozenLib$modifyPools(Consumer<? super LootPool.Builder> modifier) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}
}
