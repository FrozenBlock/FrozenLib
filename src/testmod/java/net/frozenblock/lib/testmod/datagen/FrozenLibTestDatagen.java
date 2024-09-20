/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.testmod.datagen;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.frozenblock.lib.datagen.api.FrozenBiomeTagProvider;
import net.frozenblock.lib.recipe.api.ShapedRecipeUtil;
import net.frozenblock.lib.tag.api.FrozenBlockTags;
import net.frozenblock.lib.testmod.FrozenTestMain;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.item.Instruments;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public final class FrozenLibTestDatagen implements DataGeneratorEntrypoint {

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		var pack = fabricDataGenerator.createPack();
		pack.addProvider(TestRecipeProvider::new);
		pack.addProvider(TestBiomeTagProvider::new);
		pack.addProvider(TestBlockTagProvider::new);
		pack.addProvider(TestAdvancementLootTableProvider::new);
	}

	private static class TestRecipeProvider extends FabricRecipeProvider {

		public TestRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
			super(output, registriesFuture);
		}

		@Override
		public void buildRecipes(RecipeOutput exporter) {
			ShapedRecipeUtil.withResultPatch(
				ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.GOAT_HORN)
					.define('E', Items.DRAGON_EGG)
					.pattern("EEE")
					.pattern("EEE")
					.pattern("EEE")
					.unlockedBy("has_dragon_egg", InventoryChangeTrigger.TriggerInstance.hasItems(Items.DRAGON_EGG)),
				DataComponentPatch.builder()
					.set(DataComponents.INSTRUMENT, BuiltInRegistries.INSTRUMENT.getHolderOrThrow(Instruments.DREAM_GOAT_HORN))
					.build()
			).save(exporter, FrozenTestMain.id("dream_goat_horn").toString());
		}
	}

	private static class TestBiomeTagProvider extends FrozenBiomeTagProvider {

		public TestBiomeTagProvider(FabricDataOutput output, CompletableFuture registriesFuture) {
			super(output, registriesFuture);
		}

		@Override
		protected void addTags(HolderLookup.Provider arg) {
			this.generateStructureTags();
		}

		private void generateStructureTags() {
			this.getOrCreateTagBuilder(BiomeTags.HAS_ANCIENT_CITY)
					.add(Biomes.DARK_FOREST);
		}
	}

	private static class TestBlockTagProvider extends FabricTagProvider.BlockTagProvider {

		public TestBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
			super(output, registriesFuture);
		}

		@Override
		protected void addTags(HolderLookup.Provider arg) {
			this.generateUtilityTags();
		}

		private void generateUtilityTags() {
			this.getOrCreateTagBuilder(FrozenBlockTags.DRIPSTONE_CAN_DRIP_ON)
					.add(Blocks.DIAMOND_BLOCK);
		}
	}

	private static class TestAdvancementLootTableProvider extends SimpleFabricLootTableProvider {
		public TestAdvancementLootTableProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
			super(output, registriesFuture, LootContextParamSets.ADVANCEMENT_REWARD);
		}

		@Override
		public void generate(@NotNull HolderLookup.Provider provider, BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
			output.accept(
				ResourceKey.create(Registries.LOOT_TABLE, FrozenTestMain.id("test_loottable")),
				LootTable.lootTable()
					.withPool(
						LootPool.lootPool()
							.setRolls(ConstantValue.exactly(1.0F))
							.add(LootItem.lootTableItem(Items.DRAGON_EGG))
				)
			);
		}
	}
}
