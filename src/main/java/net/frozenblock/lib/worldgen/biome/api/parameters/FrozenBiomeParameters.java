package net.frozenblock.lib.worldgen.biome.api.parameters;

import net.minecraft.world.level.biome.Climate;
import java.util.List;

public final class FrozenBiomeParameters {
	private FrozenBiomeParameters() {
		throw new UnsupportedOperationException("FrozenBiomeParameters contains only static declarations.");
	}

	public static void addWeirdness(BiomeRunnable runnable, List<Climate.Parameter> weirdnesses) {
		for (Climate.Parameter weirdness : weirdnesses) {
			runnable.run(weirdness);
		}
	}

	@FunctionalInterface
	public interface BiomeRunnable {
		void run(Climate.Parameter weirdness);
	}
}
