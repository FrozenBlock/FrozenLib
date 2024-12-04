package net.frozenblock.lib.worldgen.biome.impl;

@FunctionalInterface
public interface FrozenGrassColorModifier {
	int modifyGrassColor(double x, double z, int originalGrassColor);
}
