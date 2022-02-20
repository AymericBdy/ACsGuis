package fr.aym.acsguis.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Loads textures from internet urls
 */
public class HttpGuiTextureLoader extends GuiTextureLoader {
    private static final AtomicInteger TEXTURE_DOWNLOADER_THREAD_ID = new AtomicInteger(0);

    @Nullable
    private Thread imageThread;
    private static final Logger LOGGER = LogManager.getLogger();

    private boolean loading;
    private boolean available;
    private BufferedImage bufferedImage;

    /**
     * @param atlasTexture  The texture location
     * @param textureU      Sprite U
     * @param textureV      Sprite V
     * @param textureWidth  Sprite width
     * @param textureHeight Sprite height
     */
    public HttpGuiTextureLoader(ResourceLocation atlasTexture) {
        super(atlasTexture);
    }

    @Override
    public void loadTexture(IResourceManager resourceManager) throws IOException {
        if (!loading) {
            loading = true;

            this.deleteGlTexture();

            loadTextureFromServer(bufferedimage -> {
                this.bufferedImage = bufferedimage;
                this.available = true;
            });
        } else if (available) {
            try {
                if (bufferedImage == null) {
                    throw new FileNotFoundException("Cannot load http texture " + atlasTexture);
                }
                TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), bufferedImage, false, false);
                atlasWidth = bufferedImage.getWidth();
                atlasHeight = bufferedImage.getHeight();
            } catch (Exception e) {
                if (atlasWidth == 0 && atlasHeight == 0) {
                    atlasWidth = atlasHeight = 100;
                }
                throw e;
            }
        }
    }

    private void loadTextureFromServer(Consumer<BufferedImage> callback) {
        this.imageThread = new Thread("Texture Downloader #" + TEXTURE_DOWNLOADER_THREAD_ID.incrementAndGet()) {
            public void run() {
                HttpURLConnection httpurlconnection = null;
                HttpGuiTextureLoader.LOGGER.debug("Downloading http texture from {}", atlasTexture.toString());

                try {
                    httpurlconnection = (HttpURLConnection) (new URL(atlasTexture.toString())).openConnection(Minecraft.getMinecraft().getProxy());
                    httpurlconnection.setDoInput(true);
                    httpurlconnection.setDoOutput(false);
                    httpurlconnection.connect();

                    if (httpurlconnection.getResponseCode() / 100 == 2) {
                        BufferedImage bufferedimage;
                        bufferedimage = TextureUtil.readBufferedImage(httpurlconnection.getInputStream());

                        callback.accept(bufferedimage);
                    } else {
                        HttpGuiTextureLoader.LOGGER.error("Couldn't download http texture. Response code: " + httpurlconnection.getResponseCode() + " : " + httpurlconnection.getResponseMessage());
                        callback.accept(null);
                    }
                } catch (Exception exception) {
                    HttpGuiTextureLoader.LOGGER.error("Couldn't download http texture", exception);
                    callback.accept(null);
                } finally {
                    if (httpurlconnection != null) {
                        httpurlconnection.disconnect();
                    }
                }
            }
        };
        this.imageThread.setDaemon(true);
        this.imageThread.start();
    }

    @Override
    public String toString() {
        return "HttpGuiTextureLoader{" +
                atlasTexture +
                '}';
    }
}
