package net.frozenblock.lib.item.mixin.loot;

import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LootPool.class)
public interface LootPoolAccessor {

	@Accessor("condition")
	Optional<Holder<LootItemCondition>> frozenLib$getCondition();

	@Accessor("modifier")
	Optional<Holder<LootItemFunction>> frozenLib$getModifier();

	@Accessor("rolls")
	Holder<NumberProvider> frozenLib$getRolls();

	@Accessor("bonusRolls")
	Holder<NumberProvider> frozenLib$getBonusRolls();
}
