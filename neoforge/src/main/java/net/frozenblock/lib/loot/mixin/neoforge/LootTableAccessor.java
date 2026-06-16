package net.frozenblock.lib.loot.mixin.neoforge;

import java.util.List;
import java.util.Optional;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Exposes {@link LootTable}'s pools/functions/randomSequence for converting an existing table back into a
 * {@link LootTable.Builder}, needed to drive {@code MODIFY} from NeoForge's {@code LootTableLoadEvent}.
 */
@Mixin(LootTable.class)
public interface LootTableAccessor {
	@Accessor("pools")
	List<LootPool> frozenLib$getPools();

	@Accessor("functions")
	List<LootItemFunction> frozenLib$getFunctions();

	@Accessor("randomSequence")
	Optional<Identifier> frozenLib$getRandomSequence();
}
