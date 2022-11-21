package net.frozenblock.lib.spotting_icons.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class ClientSpottingIconMethods {

	public static boolean hasTexture(ResourceLocation resourceLocation) {
		return Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation(resourceLocation.getNamespace(),  resourceLocation.getPath())).isPresent();
	}

}
