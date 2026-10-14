/*
 * Copyright (C) 2024-2026 FrozenBlock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.registry;

import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.block.impl.clipgroup.ClipGroup;
import net.frozenblock.lib.block.impl.fire.FireType;
import net.frozenblock.lib.block.impl.sound.BlockSoundSetOverride;
import net.frozenblock.lib.block.impl.waterlike.WaterLikeType;
import net.frozenblock.lib.config.v2.entry.predicates.ConfigPredicate;
import net.frozenblock.lib.entity.api.variant.VariantSpawnInjection;
import net.frozenblock.lib.entity.impl.suffocation.SuffocationType;
import net.frozenblock.lib.integration.api.ModIntegrationSupplier;
import net.frozenblock.lib.levelgen.biome.api.attribute.BiomeEnvironmentAttributeModification;
import net.frozenblock.lib.levelgen.material.impl.MaterialRuleAddition;
import net.frozenblock.lib.levelgen.music.pitch.provider.PitchProvider;
import net.frozenblock.lib.levelgen.music.structure.api.StructureMusic;
import net.frozenblock.lib.levelgen.structure.impl.processor.StructureProcessorListAddition;
import net.frozenblock.lib.platform.RegistryHelper;
import net.frozenblock.lib.sound.api.predicate.SoundPredicate;
import net.frozenblock.lib.sound.api.type.MovingSoundType;
import net.frozenblock.lib.sound.impl.damage.PlayerDamageTypeSound;
import net.frozenblock.lib.wind.disturbance.WindDisturbanceType;
import net.frozenblock.lib.wind.extension.WindManagerExtensionType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;

@UtilityClass
public final class FrozenLibRegistries {
	public static final ResourceKey<Registry<ModIntegrationSupplier<?>>> MOD_INTEGRATION = createRegistryKey("mod_integration");
	public static final ResourceKey<Registry<Supplier<SoundPredicate<?>>>> SOUND_PREDICATE_TYPE = createRegistryKey("sound_predicate_type");
	public static final ResourceKey<Registry<WindManagerExtensionType<?>>> WIND_MANAGER_EXTENSION_TYPE = createRegistryKey("wind_manager_extension_type");
	public static final ResourceKey<Registry<WindDisturbanceType<?>>> WIND_DISTURBANCE_TYPE = createRegistryKey("wind_disturbance_type");
	public static final ResourceKey<Registry<MapCodec<? extends ConfigPredicate>>> CONFIG_PREDICATE_TYPE = createRegistryKey("config_predicate_type");
	public static final ResourceKey<Registry<MapCodec<? extends PitchProvider>>> PITCH_PROVIDER_TYPE = createRegistryKey("pitch_provider_type");
	public static final ResourceKey<Registry<MovingSoundType<?>>> MOVING_SOUND_TYPE = createRegistryKey("moving_sound_type");

	// DYNAMIC REGISTRIES
	public static final ResourceKey<Registry<ConfigPredicate>> CONFIG_PREDICATE_PROVIDER = createRegistryKey("config_predicate_provider");
	public static final ResourceKey<Registry<BlockSoundSetOverride>> BLOCK_SOUND_SET_OVERRIDE = createRegistryKey("block_sound_set_override");
	public static final ResourceKey<Registry<PitchProvider>> MUSIC_PITCH_PROVIDER = createRegistryKey("music_pitch_provider");
	public static final ResourceKey<Registry<StructureMusic>> STRUCTURE_MUSIC = createRegistryKey("structure_music");
	public static final ResourceKey<Registry<BiomeEnvironmentAttributeModification>> BIOME_ENVIRONMENT_ATTRIBUTE_MODIFICATION = createRegistryKey("biome_environment_attribute_modification");
	public static final ResourceKey<Registry<FireType>> FIRE_TYPE = createRegistryKey("fire_type");
	public static final ResourceKey<Registry<ClipGroup>> CLIP_GROUP = createRegistryKey("clip_group");
	public static final ResourceKey<Registry<WaterLikeType>> WATER_LIKE_TYPE = createRegistryKey("water_like_type");
	public static final ResourceKey<Registry<SuffocationType>> SUFFOCATION_TYPE = createRegistryKey("suffocation_type");
	public static final ResourceKey<Registry<MaterialRuleAddition>> MATERIAL_RULE_ADDITION = createRegistryKey("material_rule_addition");
	public static final ResourceKey<Registry<PlayerDamageTypeSound>> PLAYER_DAMAGE_TYPE_SOUND = createRegistryKey("player_damage_type_sound");
	public static final ResourceKey<Registry<StructureProcessorListAddition>> STRUCTURE_PROCESSOR_LIST_ADDITION = createRegistryKey("structure_processor_list_addition");
	public static final ResourceKey<Registry<VariantSpawnInjection>> VARIANT_SPAWN_INJECTION = createRegistryKey("variant_spawn_injection");

	static {
		FrozenLibBuiltInRegistries.init();
	}

	public static void init() {}

	public static void setup() {
		RegistryHelper.registerSyncedDynamicRegistry(CONFIG_PREDICATE_PROVIDER, ConfigPredicate.DIRECT_CODEC);
		RegistryHelper.registerSyncedDynamicRegistry(BLOCK_SOUND_SET_OVERRIDE, BlockSoundSetOverride.DIRECT_CODEC);
		RegistryHelper.registerSyncedDynamicRegistry(MUSIC_PITCH_PROVIDER, PitchProvider.DIRECT_CODEC);
		RegistryHelper.registerSyncedDynamicRegistry(STRUCTURE_MUSIC, StructureMusic.DIRECT_CODEC);
		RegistryHelper.registerSyncedDynamicRegistry(BIOME_ENVIRONMENT_ATTRIBUTE_MODIFICATION, BiomeEnvironmentAttributeModification.DIRECT_CODEC);
		RegistryHelper.registerSyncedDynamicRegistry(FIRE_TYPE, FireType.DIRECT_CODEC);
		RegistryHelper.registerSyncedDynamicRegistry(CLIP_GROUP, ClipGroup.DIRECT_CODEC);
		RegistryHelper.registerSyncedDynamicRegistry(WATER_LIKE_TYPE, WaterLikeType.DIRECT_CODEC);
		RegistryHelper.registerSyncedDynamicRegistry(SUFFOCATION_TYPE, SuffocationType.DIRECT_CODEC);
		RegistryHelper.registerDynamicRegistry(MATERIAL_RULE_ADDITION, MaterialRuleAddition.DIRECT_CODEC.codec());
		RegistryHelper.registerSyncedDynamicRegistry(PLAYER_DAMAGE_TYPE_SOUND, PlayerDamageTypeSound.DIRECT_CODEC);
		RegistryHelper.registerDynamicRegistry(STRUCTURE_PROCESSOR_LIST_ADDITION, StructureProcessorListAddition.DIRECT_CODEC.codec());
		RegistryHelper.registerDynamicRegistry(VARIANT_SPAWN_INJECTION, VariantSpawnInjection.CODEC);
	}

	private static <T> ResourceKey<Registry<T>> createRegistryKey(String name) {
		return ResourceKey.createRegistryKey(Registries.ROOT_REGISTRY_NAME, FrozenLibConstants.id(name));
	}
}
