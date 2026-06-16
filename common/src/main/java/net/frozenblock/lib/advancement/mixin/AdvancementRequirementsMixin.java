package net.frozenblock.lib.advancement.mixin;

import net.frozenblock.lib.advancement.impl.AdvancementRequirementsInterface;
import net.minecraft.advancements.AdvancementRequirements;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import java.util.List;

@Mixin(AdvancementRequirements.class)
public class AdvancementRequirementsMixin implements AdvancementRequirementsInterface {
	@Mutable
	@Final
	@Shadow
	private List<List<String>> requirements;

	@Override
	public List<List<String>> frozenLib$getRequirements() {
		return this.requirements;
	}

	@Override
	public void frozenLib$setRequirements(List<List<String>> requirements) {
		this.requirements = requirements;
	}
}
