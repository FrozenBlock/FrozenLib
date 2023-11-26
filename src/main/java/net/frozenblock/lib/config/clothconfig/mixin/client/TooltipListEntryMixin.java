package net.frozenblock.lib.config.clothconfig.mixin.client;

import java.util.Optional;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import me.shedaniel.clothconfig2.impl.builders.FieldBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.config.clothconfig.FrozenClothConfig;
import net.frozenblock.lib.config.clothconfig.impl.FieldBuilderInterface;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(TooltipListEntry.class)
public class TooltipListEntryMixin {

	@Inject(method = "getTooltip()Ljava/util/Optional;", at = @At("HEAD"), cancellable = true, remap = false)
	public void frozenLib$getTooltip(CallbackInfoReturnable<Optional<Component[]>> info) {
		FieldBuilder fieldBuilder = FrozenClothConfig.getFieldBuilder(TooltipListEntry.class.cast(this));
		if (fieldBuilder != null) {
			FieldBuilderInterface fieldBuilderInterface = FrozenClothConfig.getFieldBuilderInterface(fieldBuilder);
			if (!fieldBuilderInterface.getModifyType().canModify) {
				boolean present = fieldBuilderInterface.getModifyType().tooltip.isPresent();
				info.setReturnValue(
					present ?
						Optional.of(fieldBuilderInterface.getModifyType().tooltip.get().toFlatList().toArray(new Component[0]))
						:
						Optional.empty()
				);
			}
		}
	}

}
