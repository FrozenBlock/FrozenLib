package net.frozenblock.lib.cape.client.api;

import com.google.gson.JsonIOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.HttpTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class ClientCapeUtil {
	public static final Path CAPE_CACHE_PATH = Minecraft.getInstance().gameDirectory.toPath().resolve("frozenlib_cape_cache");

	public static void registerCapeTextureFromURL(
		@NotNull ResourceLocation capeLocation, ResourceLocation capeTextureLocation, String textureURL
	) throws JsonIOException {
		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public ResourceLocation getFabricId() {
				return capeLocation;
			}

			@Override
			public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
				CompletableFuture<ResourceLocation> completableFuture = new CompletableFuture<>();
				HttpTexture httpTexture = new HttpTexture(
					CAPE_CACHE_PATH.resolve(capeLocation.getNamespace()).resolve(capeLocation.getPath() + ".png").toFile(),
					textureURL,
					DefaultPlayerSkin.getDefaultTexture(),
					false,
					() -> completableFuture.complete(capeTextureLocation)
				);
				Minecraft.getInstance().getTextureManager().register(capeTextureLocation, httpTexture);
			}
		});
	}
}
