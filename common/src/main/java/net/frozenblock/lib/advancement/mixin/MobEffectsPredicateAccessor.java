package net.frozenblock.lib.advancement.mixin;

import net.minecraft.advancements.predicates.MobEffectsPredicate;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.Map;

@Mixin(MobEffectsPredicate.class)
public interface MobEffectsPredicateAccessor {

	@Accessor("effectMap")
	void frozenLib$setEffectMap(Map<Holder<MobEffect>, MobEffectsPredicate.MobEffectInstancePredicate> effectMap);
}
