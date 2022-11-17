package net.frozenblock.lib.testmod.datagen;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.frozenblock.lib.datagen.api.FrozenBiomeTagProvider;
import net.frozenblock.lib.tags.FrozenBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;

public final class FrozenLibTestDatagen implements DataGeneratorEntrypoint {

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		var pack = fabricDataGenerator.createPack();
		pack.addProvider(TestBiomeTagProvider::new);
		pack.addProvider(TestBlockTagProvider::new);
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
}
