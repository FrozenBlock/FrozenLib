package net.frozenblock.lib.config.v2.entry.predicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.config.v2.registry.ConfigV2Registry;
import net.frozenblock.lib.config.v2.registry.ID;

public class ExistsPredicate implements ConfigPredicate {
	public static final MapCodec<ExistsPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ID.CODEC.fieldOf("entry").forGetter(predicate -> predicate.id)
	).apply(instance, ExistsPredicate::new));
	private final ID id;
	private Boolean exists = null;

	public ExistsPredicate(ID id) {
		this.id = id;
	}

	@Override
	public Boolean get() {
		if (this.exists == null) this.exists = ConfigV2Registry.getEntry(this.id) != null;
		return this.exists;
	}

	@Override
	public ConfigPredicateType<?> type() {
		return ConfigPredicateType.EXISTS;
	}
}
