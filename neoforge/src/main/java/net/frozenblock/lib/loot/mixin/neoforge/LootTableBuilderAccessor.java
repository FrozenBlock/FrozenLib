package net.frozenblock.lib.loot.mixin.neoforge;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Exposes {@link LootTable.Builder}'s pools/functions builders, needed to copy an existing {@link LootTable}
 * into a fresh builder for the {@code MODIFY} event on NeoForge.
 */
@Mixin(LootTable.Builder.class)
public interface LootTableBuilderAccessor {
	@Accessor("pools")
	ImmutableList.Builder<LootPool> frozenLib$getPools();

	@Accessor("functions")
	ImmutableList.Builder<LootItemFunction> frozenLib$getFunctions();
}
