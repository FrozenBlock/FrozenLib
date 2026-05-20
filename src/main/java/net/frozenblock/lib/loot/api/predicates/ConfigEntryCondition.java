package net.frozenblock.lib.loot.api.predicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.config.v2.entry.ConfigEntry;
import net.frozenblock.lib.config.v2.entry.data.ConfigEntryPredicate;
import net.frozenblock.lib.config.v2.registry.ID;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ConfigEntryCondition implements LootItemCondition {
	public static final MapCodec<ConfigEntryCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ConfigEntryPredicate.CODEC.fieldOf("config_entry_predicate").forGetter(config -> config.configEntryPredicate)
	).apply(instance, ConfigEntryCondition::new));
	private final ConfigEntryPredicate<?> configEntryPredicate;

	public ConfigEntryCondition(ConfigEntryPredicate<?> configEntryPredicate) {
		this.configEntryPredicate = configEntryPredicate;
	}

	public static <T> ConfigEntryCondition of(ID entryId, ConfigEntryPredicate.Operator operator, T target) {
		return new ConfigEntryCondition(new ConfigEntryPredicate<>(entryId, operator, target));
	}

	public static <T> ConfigEntryCondition equalTo(ConfigEntry<T> entry, T target) {
		return of(entry.id(), ConfigEntryPredicate.Operator.EQUAL_TO, target);
	}

	public static <T> ConfigEntryCondition notEqualTo(ConfigEntry<T> entry, T target) {
		return of(entry.id(), ConfigEntryPredicate.Operator.NOT_EQUAL_TO, target);
	}

	public static <T> ConfigEntryCondition greaterThan(ConfigEntry<T> entry, T target) {
		return of(entry.id(), ConfigEntryPredicate.Operator.GREATER_THAN, target);
	}

	public static <T> ConfigEntryCondition lessThan(ConfigEntry<T> entry, T target) {
		return of(entry.id(), ConfigEntryPredicate.Operator.LESS_THAN, target);
	}

	@Override
	public MapCodec<? extends LootItemCondition> codec() {
		return MAP_CODEC;
	}

	@Override
	public boolean test(LootContext lootContext) {
		return this.configEntryPredicate.evaluate();
	}
}
