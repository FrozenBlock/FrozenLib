package net.frozenblock.lib.cape.api;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.frozenblock.lib.cape.impl.Cape;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public class CapeRegistry {
	public static final Cape DUMMY_CAPE = new Cape(null, null);
	private static final Map<ResourceLocation, List<UUID>> CAPE_RESTRICTIONS = new HashMap<>();
	private static final ArrayList<Cape> CAPES = new ArrayList<>();

	public static @NotNull @Unmodifiable List<Cape> getCapes() {
		return ImmutableList.copyOf(CAPES);
	}

	public static boolean canPlayerUserCape(UUID uuid, ResourceLocation texture) {
		List<UUID> allowedUUIDs = CAPE_RESTRICTIONS.get(texture);
		if (allowedUUIDs == null) return true;
		return allowedUUIDs.contains(uuid);
	}

	public static void registerCape(ResourceLocation cape) {
		CAPES.add(new Cape(cape, buildCapeTextureLocation(cape)));
	}

	public static void registerRestrictedCape(ResourceLocation cape, List<UUID> allowedPlayers) {
		ResourceLocation textureLocation = buildCapeTextureLocation(cape);
		CAPES.add(new Cape(cape, textureLocation));
		CAPE_RESTRICTIONS.put(textureLocation, allowedPlayers);
	}

	private static ResourceLocation buildCapeTextureLocation(@NotNull ResourceLocation cape) {
		return ResourceLocation.tryBuild(cape.getNamespace(), "textures/cape/" + cape.getPath() + ".png");
	}
}
