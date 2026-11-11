package net.frozenblock.lib.levelgen.biome.api.modifications.data;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.levelgen.biome.api.modifications.BiomeModificationContext;
import net.frozenblock.lib.levelgen.biome.api.modifications.ModificationPhase;
import net.frozenblock.lib.levelgen.biome.api.modifications.data.selector.BiomeSelector;
import net.minecraft.core.Holder;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.level.storage.loot.ValidationContext;

public class EnvironmentAttributeModification extends BiomeModification {
	private static final String ENVIRONMENT_ATTRIBUTE_MAP_KEY = "attributes";
	public static final MapCodec<EnvironmentAttributeModification> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		SELECTOR_MAP_CODEC.forGetter(EnvironmentAttributeModification::selector),
		MODIFICATION_PHASE_MAP_CODEC.forGetter(EnvironmentAttributeModification::phase),
		EnvironmentAttributeMap.CODEC_ONLY_POSITIONAL.fieldOf(ENVIRONMENT_ATTRIBUTE_MAP_KEY).forGetter(modification -> modification.attributes)
	).apply(instance, EnvironmentAttributeModification::new));
	private final EnvironmentAttributeMap attributes;

	protected EnvironmentAttributeModification(Holder<BiomeSelector> selector, ModificationPhase phase, EnvironmentAttributeMap attributes) {
		super(selector, phase);
		this.attributes = attributes;
	}

	@Override
	public void modify(BiomeModificationContext context) {
		context.getAttributes().addAll(this.attributes);
	}

	@Override
	MapCodec<? extends BiomeModification> codec() {
		return CODEC;
	}

	@Override
	public void validate(ValidationContext context) {
		super.validate(context);
		if (this.attributes.equals(EnvironmentAttributeMap.EMPTY)) context.reportProblem(new EmptyModificationProblem(ENVIRONMENT_ATTRIBUTE_MAP_KEY));
	}
}
