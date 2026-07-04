package net.frozenblock.lib.item.mixin.loot;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;
import net.frozenblock.lib.item.api.loot.FrozenLibLootTableBuilder;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LootTable.Builder.class)
abstract class LootTableBuilderMixin implements FrozenLibLootTableBuilder {

	@Shadow
	@Final
	@Mutable
	public ImmutableList.Builder<LootPool> pools;

	@Unique
	private LootTable.Builder frozenLib$self() {
		return (LootTable.Builder) (Object) this;
	}

	@Override
	public LootTable.Builder frozenLib$modifyPools(Consumer<? super LootPool.Builder> modifier) {
		final List<LootPool> list = new ArrayList<>(this.pools.build());
		final ListIterator<LootPool> iterator = list.listIterator();

		while (iterator.hasNext()) {
			final LootPool.Builder poolBuilder = frozenLib$copyOf(iterator.next());
			modifier.accept(poolBuilder);
			iterator.set(poolBuilder.build());
		}

		this.pools = ImmutableList.builder();
		this.pools.addAll(list);

		return frozenLib$self();
	}

	@Unique
	private static LootPool.Builder frozenLib$copyOf(LootPool pool) {
		final LootPool.Builder builder = LootPool.lootPool();
		builder.setRolls(pool.rolls);
		builder.setBonusRolls(pool.bonusRolls);
		pool.entries.forEach(builder.entries::add);
		pool.conditions.forEach(builder.conditions::add);
		pool.functions.forEach(builder.functions::add);
		return builder;
	}
}
