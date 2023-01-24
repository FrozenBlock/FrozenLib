package net.frozenblock.lib.silentticker;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class SilentTickerManager {

	private static final List<SilentTicker> SILENT_TICKERS = new ArrayList<>();
	private static final Map<ResourceLocation, Pair<Class<? extends SilentTicker>, SilentTickerFactory<? extends SilentTicker>>> SILENT_TICKER_REGISTRY = new LinkedHashMap<>();
	private static int nextId;

	public static void tickSilentTickers() {
		SILENT_TICKERS.forEach(SilentTicker::baseTick);
		SILENT_TICKERS.removeIf(SilentTicker::isRemoved);
	}

	public static <T extends SilentTicker> void addSilentTicker(T silentTicker) {
		SILENT_TICKERS.add(silentTicker);
	}

	public static int getNextIdAndIncrement() {
		nextId += 1;
		return nextId;
	}

	public static void register(ResourceLocation location, Class<? extends SilentTicker> silentTicker, SilentTickerFactory<? extends SilentTicker> factory) {
		SILENT_TICKER_REGISTRY.put(location, Pair.of(silentTicker, factory));
	}

	@Nullable
	public static SilentTicker getById(int id) {
		for (SilentTicker ticker : SILENT_TICKERS) {
			if (ticker.getId() == id) {
				return ticker;
			}
		}
		return null;
	}


	@Nullable
	public static Class<? extends SilentTicker> getTickerClass(ResourceLocation location) {
		if (SILENT_TICKER_REGISTRY.containsKey(location)) {
			return SILENT_TICKER_REGISTRY.get(location).getFirst();
		}
		return null;
	}

	@Nullable
	public static SilentTickerFactory<? extends SilentTicker> getTickerFactory(ResourceLocation location) {
		if (SILENT_TICKER_REGISTRY.containsKey(location)) {
			return SILENT_TICKER_REGISTRY.get(location).getSecond();
		}
		return null;
	}

	@Nullable
	public static ResourceLocation getTickerResourceLocation(Class<? extends SilentTicker> silentTicker) {
		for (Map.Entry<ResourceLocation, Pair<Class<? extends SilentTicker>, SilentTickerFactory<? extends SilentTicker>>> entry : SILENT_TICKER_REGISTRY.entrySet()) {
			if (entry.getValue().getFirst() == silentTicker) {
				return entry.getKey();
			}
		}
		return null;
	}

	public interface SilentTickerFactory<T extends SilentTicker> {
		T create(Level level, Vec3 pos, int ticks, int id, FriendlyByteBuf byteBuf);
	}

}
