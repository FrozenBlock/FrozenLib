package net.frozenblock.lib.image_transfer.client;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.frozenblock.lib.image_transfer.FileTransferPacket;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class ServerTexture extends SimpleTexture {
    public static final Map<String, ServerTexture> WAITING_TEXTURES = new HashMap<>();
    private static final Logger LOGGER = LogUtils.getLogger();
    private final File file;
    private final String destPath;
    private final String fileName;
    @Nullable
    private final Runnable onDownloaded;
    @Nullable
    private CompletableFuture<?> future;
    private boolean uploaded;

    public ServerTexture(String destPath, String fileName, ResourceLocation fallback, @Nullable Runnable callback) {
        super(fallback);
        this.file = Minecraft.getInstance().gameDirectory.toPath().resolve(destPath).resolve(fileName).toFile();
        this.destPath = destPath;
        this.fileName = fileName;
        this.onDownloaded = callback;
        WAITING_TEXTURES.put(this.destPath + "/" + this.fileName, this);
    }

    private void loadCallback(NativeImage image) {
        if (this.onDownloaded != null) {
            this.onDownloaded.run();
        }

        Minecraft.getInstance().execute(() -> {
            this.uploaded = true;
            if (!RenderSystem.isOnRenderThread()) {
                RenderSystem.recordRenderCall(() -> this.upload(image));
            } else {
                this.upload(image);
            }
        });
    }

    private void upload(@NotNull NativeImage image) {
        TextureUtil.prepareImage(this.getId(), image.getWidth(), image.getHeight());
        image.upload(0, 0, 0, true);
    }

    public void runFutureForTexture() {
        if (this.future == null) {
            this.future = CompletableFuture.runAsync(() -> {
                LOGGER.debug("Downloading server texture {}", this.fileName);
                try {
                    InputStream inputStream = new FileInputStream(this.file);
                    Minecraft.getInstance().execute(() -> {
                        NativeImage nativeImagex = this.load(inputStream);
                        if (nativeImagex != null) {
                            this.loadCallback(nativeImagex);
                        }
                    });
                } catch (Exception e) {
                    LOGGER.error("Couldn't download server texture", e);
                }
            }, Util.backgroundExecutor());
        }
    }

    @Override
    public void load(ResourceManager manager) throws IOException {
        Minecraft.getInstance().execute(() -> {
            if (!this.uploaded) {
                try {
                    super.load(manager);
                } catch (IOException var3x) {
                    LOGGER.warn("Failed to load server texture: {}", this.location, var3x);
                }

                this.uploaded = true;
            }
        });

        NativeImage nativeImage;
        if (this.file != null && this.file.exists() && this.file.isFile()) {
            LOGGER.debug("Loading server texture from local cache ({})", this.file);
            FileInputStream fileInputStream = new FileInputStream(this.file);
            nativeImage = this.load(fileInputStream);
            fileInputStream.close();
        } else {
			ClientPlayNetworking.send(FileTransferPacket.createRequest(this.destPath, this.fileName));
            nativeImage = null;
        }

        if (nativeImage != null) {
            this.loadCallback(nativeImage);
        }
    }

    @Nullable
    private NativeImage load(InputStream stream) {
        NativeImage nativeImage = null;

        try {
            nativeImage = NativeImage.read(stream);
        } catch (Exception e) {
            LOGGER.warn("Error while loading server texture", e);
        }

        return nativeImage;
    }
}
