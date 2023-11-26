package net.frozenblock.lib.config.clothconfig.mixin.client;

import java.lang.reflect.Field;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.Requirement;
import me.shedaniel.clothconfig2.impl.builders.FieldBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.FrozenLogUtils;
import net.frozenblock.lib.config.api.annotation.FieldIdentifier;
import net.frozenblock.lib.config.api.network.ConfigSyncModification;
import net.frozenblock.lib.config.clothconfig.impl.AbstractConfigListEntryInterface;
import net.frozenblock.lib.config.clothconfig.impl.FieldBuilderInterface;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(FieldBuilder.class)
public class FieldBuilderMixin<T, A extends AbstractConfigListEntry, SELF extends FieldBuilder<T, A, SELF>> implements FieldBuilderInterface {

	@Shadow
	protected Requirement enableRequirement;

	@Unique
	private ConfigSyncModification.ModifyType frozenLib$modifyType = ConfigSyncModification.ModifyType.CAN_MODIFY;

	@Inject(method = "finishBuilding", at = @At(value = "RETURN", ordinal = 1), remap = false)
	public void frozenLib$finishBuilding(A gui, CallbackInfoReturnable<A> cir) {
		((AbstractConfigListEntryInterface)gui).setFieldBuilder(FieldBuilder.class.cast(this));
	}

	@Override
	public void addSyncData(@NotNull Class<?> clazz, String identifier) {
		Field field = null;
		for (Field fieldToCheck : clazz.getDeclaredFields()) {
			if (fieldToCheck.isAnnotationPresent(FieldIdentifier.class)) {
				if (fieldToCheck.getAnnotation(FieldIdentifier.class).identifier().equals(identifier)) {
					if (field != null) FrozenLogUtils.error("Multiple fields in " + clazz.getName() + " contain identifier " + identifier + "!", true, null);
					field = fieldToCheck;
				}
			}
		}
		Field finalField = field;
		Requirement nonSyncRequirement = () -> {
			this.frozenLib$modifyType = ConfigSyncModification.canModifyField(finalField);
			return this.frozenLib$modifyType.canModify;
		};
		nonSyncRequirement.check();
		if (this.enableRequirement != null) {
			this.setRequirement(Requirement.all(this.enableRequirement, nonSyncRequirement));
		} else {
			this.setRequirement(nonSyncRequirement);
		}
	}

	@Override
	public ConfigSyncModification.ModifyType getModifyType() {
		return this.frozenLib$modifyType;
	}

	@Shadow
	public final SELF setRequirement(Requirement requirement) {
		throw new AssertionError("Mixin injection failed - FrozenLib FieldBuilderMixin.");
	}

}
