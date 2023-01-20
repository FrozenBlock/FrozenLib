package net.frozenblock.lib.worldgen.biome.api;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import net.minecraft.world.level.biome.Climate;
import java.util.ArrayList;

public class BiomeParameters {
	public final ArrayList<Climate.Parameter> temperatures = new ArrayList<>();
	public final ArrayList<Climate.Parameter> humidities = new ArrayList<>();
	public final ArrayList<Climate.Parameter> continentalnesses = new ArrayList<>();
	public final ArrayList<Climate.Parameter> erosions = new ArrayList<>();
	public final ArrayList<Climate.Parameter> depths = new ArrayList<>();
	public final ArrayList<Climate.Parameter> weirdnesses = new ArrayList<>();
	public final FloatArrayList offsets = new FloatArrayList();

	public BiomeParameters() {
	}
}
