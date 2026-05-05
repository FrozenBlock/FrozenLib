package net.frozenblock.lib.block.mixin.fire;

import net.frozenblock.lib.block.api.fire.FireTypes;
import net.frozenblock.lib.block.impl.fire.FireData;
import net.frozenblock.lib.block.impl.fire.FireType;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.BaseFireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Optional;

@Mixin(BaseFireBlock.class)
public class BaseFireBlockMixin {

	@Inject(method = "lambda$entityInside$0", at = @At("HEAD"))
	public void frozenLib$setFireType(Entity entity, CallbackInfo info) {
		final Optional<Holder<FireType>> fireType = FireTypes.getTypeForBlock(entity.registryAccess(), BaseFireBlock.class.cast(this));
		if (fireType.isEmpty()) return;

		FireData.trySet(entity, fireType.get());
	}
}
